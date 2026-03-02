const { spawn, execSync } = require('child_process');
const net = require('net');

const apps = [
    { name: 'API Server', command: 'pnpm', args: ['dev:api'], port: 8080, required: true },
    { name: 'Admin Dashboard', command: 'pnpm', args: ['dev:admin'], port: 4200 },
    { name: 'Public Site', command: 'pnpm', args: ['dev:public'], port: 4201 }
];

const infra = [
    { name: 'PostgreSQL', port: 5432 },
    { name: 'Redis', port: 6379 },
    { name: 'MinIO', port: 9000 }
];

async function isPortOpen(port) {
    return new Promise((resolve) => {
        const socket = new net.Socket();
        const onError = () => {
            socket.destroy();
            resolve(false);
        };
        socket.setTimeout(1000);
        socket.on('error', onError);
        socket.on('timeout', onError);
        socket.connect(port, '127.0.0.1', () => {
            socket.destroy();
            resolve(true);
        });
    });
}

function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function checkInfra() {
    console.log('\n🔍 Checking Infrastructure...');
    const missing = [];
    for (const item of infra) {
        const open = await isPortOpen(item.port);
        if (open) {
            console.log(`  ✅ ${item.name} is running on port ${item.port}`);
        } else {
            console.log(`  ❌ ${item.name} is NOT running on port ${item.port}`);
            missing.push(item);
        }
    }
    return missing;
}

async function waitForPort(port, name) {
    process.stdout.write(`⏳ Waiting for ${name} to be ready on port ${port}...`);
    let attempts = 0;
    while (!(await isPortOpen(port))) {
        attempts++;
        if (attempts % 5 === 0) process.stdout.write('.');
        await wait(2000);
        if (attempts > 30) { // 1 minute timeout for apps
            console.log(`\n❌ Timeout waiting for ${name}.`);
            return false;
        }
    }
    console.log(`\n✅ ${name} is ready!`);
    return true;
}

async function start() {
    console.log('🚀 Starting Orthopedic Platform Setup...');

    const missingInfra = await checkInfra();
    if (missingInfra.length > 0) {
        console.log('\n⚠️  Required infrastructure is missing.');
        try {
            execSync('docker --version', { stdio: 'ignore' });
            console.log('📦 Docker detected! Attempting to start infrastructure via Docker Compose...');
            spawn('docker-compose', ['up', '-d', 'db', 'redis', 'minio'], { stdio: 'inherit', shell: true });
            console.log('⏳ Waiting for infrastructure to initialize...');
            await wait(5000);
        } catch (e) {
            console.log('\n❌ Docker not found. Please start PostgreSQL, Redis, and MinIO manually.');
            console.log('   Expected ports: 5432 (DB), 6379 (Redis), 9000 (MinIO)');
        }
    }

    for (const app of apps) {
        console.log(`\n▶️  Starting ${app.name}...`);
        const proc = spawn(app.command, app.args, {
            stdio: 'inherit',
            shell: true
        });

        proc.on('error', (err) => {
            console.error(`  ❌ Failed to start ${app.name}:`, err);
        });

        const ready = await waitForPort(app.port, app.name);
        if (!ready && app.required) {
            console.log(`\n🛑 Critical service ${app.name} failed to start.`);
            console.log('   Check if Java 21 is installed and your database is accessible.');
            if (app.name === 'API Server') {
                console.log('   Continuing with frontends, but they will have limited functionality...');
            }
        }
    }

    console.log('\n✨ Startup sequence initiated! Use Ctrl+C to stop all services.');
}

start().catch(err => {
    console.error('Fatal error during startup:', err);
    process.exit(1);
});
