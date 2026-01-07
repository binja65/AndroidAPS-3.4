# Project Preferences

## User Preferences

- **When user says "ALL":**
  - Do NOT optimize, batch, summarize, or take shortcuts
  - Create a todo item for EVERY item/file to be processed
  - Process each item individually and mark complete only after fully done
  - After each batch, state how many remain and continue until zero remain
  - Do NOT stop early or claim "done" until truly everything is processed
  - User WILL verify results - assume accountability
- **Always use explicit imports:**
  - Never use fully qualified names (e.g., `kotlin.math.abs`)
  - Always add proper import statements at the top of the file
  - Example: Add `import kotlin.math.abs` instead of using `kotlin.math.abs()`
- On KSP error during compilation just compile again. Do not clean build.
- On file locked error stop gradle daemons
- Never install app automatically
- Use %TEMP% directory for screenshots
- Check what OS is running and use commands for this OS
- Check for WSL availability, use WSL for Linux/shell commands if running on Windows
- Use Python from WSL: `wsl python3` when WSL is available on Windows
- Detect project path in WSL at start
- Can edit files and run commands freely without asking for permission
- Can use internet/web search as needed
- For compilation do not use gradle daemon
- Avoid duplication while writing new code and resources. Prefer moving to another module. Elaborate if you think, it's necessary

## Project Info

- Android project (AndroidAPS - open source artificial pancreas system)
- Main branch: `master`
- Development branch: `dev`
