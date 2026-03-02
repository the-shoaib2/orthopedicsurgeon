import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotpSetup } from './totp-setup';

describe('TotpSetup', () => {
  let component: TotpSetup;
  let fixture: ComponentFixture<TotpSetup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotpSetup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TotpSetup);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
