import requests

API_KEY = "AIzaSyBpd8DV3CxynYycrEjKaqjga4AueVJiJos"
PROJECT_ID = "ottoscents-smart-shelf"
AUTH_URL = f"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key={API_KEY}"
FIRESTORE_URL = f"https://firestore.googleapis.com/v1/projects/{PROJECT_ID}/databases/(default)/documents/users"

def provision_user(email, password, role, branch):
    print(f"Provisioning {email}...")
    # 1. Create User
    response = requests.post(AUTH_URL, json={
        "email": email,
        "password": password,
        "returnSecureToken": True
    })
    
    data = response.json()
    if response.status_code == 200:
        uid = data['localId']
        print(f" - Auth Success. UID: {uid}")
        
        # 2. Set Role in Firestore
        doc_url = f"{FIRESTORE_URL}/{uid}"
        patch_res = requests.patch(doc_url, json={
            "fields": {
                "role": { "stringValue": role },
                "branch": { "stringValue": branch }
            }
        })
        if patch_res.status_code == 200:
            print(f" - Firestore Success. Role: {role}, Branch: {branch}")
        else:
            print(f" - Firestore Error: {patch_res.text}")
    else:
        error_msg = data.get('error', {}).get('message', 'Unknown Error')
        if error_msg == "EMAIL_EXISTS":
            print(f" - Error: {email} already exists. Attempting to get UID by logging in...")
            # If user exists, log in to get UID and update role
            login_url = f"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={API_KEY}"
            login_res = requests.post(login_url, json={
                "email": email,
                "password": password,
                "returnSecureToken": True
            })
            if login_res.status_code == 200:
                uid = login_res.json()['localId']
                print(f" - Login Success. Updating Firestore for existing user (UID: {uid})...")
                patch_res = requests.patch(f"{FIRESTORE_URL}/{uid}", json={
                    "fields": {
                        "role": { "stringValue": role },
                        "branch": { "stringValue": branch }
                    }
                })
                if patch_res.status_code == 200:
                    print(f" - Firestore Update Success. Role: {role}, Branch: {branch}")
            else:
                print(f" - Login Error. Cannot update existing user. Reason: {login_res.text}")
        else:
            print(f" - Auth Error: {error_msg}")

if __name__ == "__main__":
    print("Starting automated provisioning...\n")
    provision_user("admin@ottoscents.com", "password123", "admin", "San Pablo")
    print("-" * 30)
    provision_user("staff@ottoscents.com", "password123", "staff", "Lipa")
    print("\nDone.")
