import shlex, subprocess
from pathlib import Path
line = next(x for x in Path('/root/work/iob-runclient-info.log').read_text(errors='replace').splitlines() if "Working directory: /root/work/iob-packaged-client-harness-20260820/run Command:" in x)
cmd = shlex.split(line.split(' Command: ', 1)[1])
# Use Forge's production/SRG client artifact, not the official-mapped userdev artifact.
cmd = [('/root/.gradle/caches/forge_gradle/minecraft_user_repo/net/minecraftforge/forge/1.18.2-40.3.12/forge-1.18.2-40.3.12-srg.jar' if x.endswith('forge-1.18.2-40.3.12_mapped_official_1.18.2.jar') else x) for x in cmd]
# Run the normal Forge client launch service against the production/SRG domain.
cmd.insert(1, '-DlibraryDirectory=/root/work/iob-sol007-runtime-20260819/libraries')
cmd.insert(1, '-Dforge.gameTestServer=false')
for i, x in enumerate(cmd):
    if x == 'cpw.mods.bootstraplauncher.BootstrapLauncher':
        pass
    if x == '--launchTarget' and i + 1 < len(cmd):
        cmd[i + 1] = 'forgeclient'
cmd = [x for x in cmd if x not in ('--version', 'MOD_DEV')]
cmd = [x for x in cmd if not x.startswith('-Dmixin.env.refMapRemappingFile=') and x != '-Dmixin.env.remapRefMap=true']
print('EXEC', ' '.join(shlex.quote(x) for x in cmd), flush=True)
raise SystemExit(subprocess.run(cmd, cwd='/root/work/iob-packaged-client-harness-20260820/run-srg-client3').returncode)
