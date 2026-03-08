import sys

def read_log(filename):
    # Try different encodings or use errors='ignore'
    encodings = ['utf-16', 'utf-8', 'cp949', 'latin-1']
    for enc in encodings:
        try:
            with open(filename, 'r', encoding=enc) as f:
                content = f.read()
                print(f"--- Decoded with {enc} ---")
                # Print last 500 lines for brevity to see current test logs
                lines = content.splitlines()
                for line in lines[-500:]:
                    print(line)
                return
        except Exception:
            continue
    
    # Fallback to binary read and ignore errors
    print("--- Fallback: Binary read with ignore ---")
    with open(filename, 'rb') as f:
        print(f.read().decode('utf-8', errors='ignore')[-2000:])

if __name__ == "__main__":
    if len(sys.argv) > 1:
        read_log(sys.argv[1])
    else:
        print("Please provide a filename.")
