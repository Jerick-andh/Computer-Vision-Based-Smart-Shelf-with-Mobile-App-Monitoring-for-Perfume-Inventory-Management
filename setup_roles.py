import requests
import json
import getpass
import sys

# Firebase Project Configuration
API_KEY = "AIzaSyBpd8DV3CxynYycrEjKaqjga4AueVJiJos"
PROJECT_ID = "ottoscents-smart-shelf"

# REST API Endpoints
AUTH_URL = f"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key={API_KEY}"
FIRESTORE_URL = f"https://firestore.googleapis.com/v1/projects/{PROJECT_ID}/databases/(default)/documents/users"

def create_user(email, password):
    payload = {
        "email": email,
        "password": password,
        "returnSecureToken": True
    }
    print(f"\n[1/2] Registering user {email} in Firebase Authentication...")
    response = requests.post(AUTH_URL, json=payload)
    data = response.json()
    
    if response.status_code == 200:
        uid = data['localId']
        print(f"      Success! User UID generated: {uid}")
        return uid
    else:
        error_msg = data.get('error', {}).get('message', 'Unknown Error')
        if error_msg == "EMAIL_EXISTS":
            print("      Error: The email address is already in use by another account.")
            print("      To fix this: Go to Firebase Console -> Authentication -> Delete the user, then try again.")
        else:
            print(f"      Error registering user: {error_msg}")
        return None

def configure_role(uid, role, branch):
    print(f"[2/2] Configuring Firestore permissions for UID: {uid}...")
    doc_url = f"{FIRESTORE_URL}/{uid}"
    
    payload = {
        "fields": {
            "role": { "stringValue": role },
            "branch": { "stringValue": branch }
        }
    }
    
    response = requests.patch(doc_url, json=payload)
    
    if response.status_code == 200:
        print(f"      Success! {role.capitalize()} role and branch '{branch}' linked to the account.")
    else:
        print(f"      Error saving to Firestore: {response.status_code}")
        print(response.text)

def main():
    print("=" * 50)
    print(" Smart Shelf - Firebase Role & Account Configurator")
    print("=" * 50)
    print("This script will create a new user account and immediately")
    print("configure their Admin or Staff privileges in the database.\n")
    
    print("Choose Account Type:")
    print("1. Admin (Full access to Reports, System Logs, Settings)")
    print("2. Staff (Restricted access, primarily for Inventory Management)")
    
    choice = input("\nEnter choice (1 or 2): ").strip()
    if choice == '1':
        role = "admin"
    elif choice == '2':
        role = "staff"
    else:
        print("Invalid choice. Exiting.")
        sys.exit(1)
        
    email = input(f"\nEnter {role.capitalize()} Email: ").strip()
    password = getpass.getpass(f"Enter Password (min 6 chars): ")
    branch = input("Enter Assigned Branch (e.g., San Pablo, Lipa): ").strip()
    
    if len(password) < 6:
        print("\nError: Password must be at least 6 characters long.")
        sys.exit(1)
        
    uid = create_user(email, password)
    if uid:
        configure_role(uid, role, branch)
        print("\n" + "=" * 50)
        print(" ALL DONE! 🎉")
        print(f" You can now log into the Smart Shelf Android App using:")
        print(f" Email: {email}")
        print(f" Password: {'*' * len(password)}")
        print("=" * 50)

if __name__ == "__main__":
    main()
