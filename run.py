import subprocess
import os
import time

def main():
    root_dir = os.path.dirname(os.path.abspath(__file__))
    backend_dir = os.path.join(root_dir, "backend")

    print("--- Nextstay Project Starter ---")

    # 1. Start Docker MySQL
    print("\n[1/2] Checking Docker MySQL...")
    subprocess.run("docker-compose up -d", shell=True, cwd=root_dir)
    time.sleep(1)

    # 2. Run Spring Boot in a NEW CMD window
    print("\n[2/2] Starting Spring Boot Backend in a new window...")
    print("Tip: Access Swagger at http://localhost:8080/swagger-ui/index.html")
    
    # Windows 'start' command open a new window
    # /K keeps the window open after the command finishes (useful for seeing errors)
    # cd /d ensures we are in the correct directory regardless of drive
    cmd = f'start cmd /K "cd /d {backend_dir} && title Nextstay-Backend && ./gradlew bootRun"'
    
    subprocess.run(cmd, shell=True)
    
    print("\nDone! The backend is starting in the new window.")

if __name__ == "__main__":
    main()
