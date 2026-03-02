import { Directive, Input, TemplateRef, ViewContainerRef, inject, effect } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Role } from '@repo/types';

@Directive({
  selector: '[hasPermission]',
  standalone: true
})
export class HasPermissionDirective {
  private auth = inject(AuthService);
  private templateRef = inject(TemplateRef<any>);
  private viewContainer = inject(ViewContainerRef);

  private permissions: Role[] = [];
  private logicalOp: 'AND' | 'OR' = 'OR';

  @Input() set hasPermission(val: Role | Role[]) {
    this.permissions = Array.isArray(val) ? val : [val];
    this.updateView();
  }

  @Input() set hasPermissionOp(op: 'AND' | 'OR') {
    this.logicalOp = op;
    this.updateView();
  }

  constructor() {
    // Re-evaluate when user state changes
    effect(() => {
      this.auth.currentUser();
      this.updateView();
    });
  }

  private updateView() {
    const user = this.auth.currentUser();
    if (!user) {
      this.viewContainer.clear();
      return;
    }

    const userRoles = user.roles || [];
    let hasAccess = false;

    if (this.logicalOp === 'OR') {
      hasAccess = this.permissions.some(role => userRoles.includes(role));
    } else {
      hasAccess = this.permissions.every(role => userRoles.includes(role));
    }

    if (hasAccess) {
      if (this.viewContainer.length === 0) {
        this.viewContainer.createEmbeddedView(this.templateRef);
      }
    } else {
      this.viewContainer.clear();
    }
  }
}
