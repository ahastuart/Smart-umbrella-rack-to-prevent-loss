import firebase_admin
from firebase_admin import credentials, db, storage
import datetime
import time

cred = credentials.Certificate('ummproject-a34a2-firebase-adminsdk-olr6g-719515e5db.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://ummproject-a34a2-default-rtdb.firebaseio.com/',
    'storageBucket': 'ummproject-a34a2.appspot.com'
})

database = db.reference()
storage_client = storage.bucket()

def upload_image_to_storage(image_path):
    blob = storage_client.blob(image_path)
    blob.upload_from_filename(image_path)
    return blob.public_url

def save_data_to_firebase(match_data):
    umbrella_img_url = upload_image_to_storage(match_data['umbrella_img_path'])
    person_img_url = upload_image_to_storage(match_data['person_img_path'])
    
    current_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # Realtime Database
    database.child('matched_data').child(match_data['umbrella_id']).set({
        'umbrella_id': match_data['umbrella_id'],
        'person_id': match_data['person_id'],
        'umbrella_img_url': umbrella_img_url,
        'person_img_url': person_img_url,
        'timestamp': current_time
    })
    

def save_umbrella_stand_data(slot_number, status, distance, umbrella_id=None):
    timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
    data = {
        "slot_number": slot_number,
        "status": status,
        "distance": distance,
        "timestamp": timestamp
    }
    if umbrella_id is not None:
        data["umbrella_id"] = str(umbrella_id)
    else:
        data["umbrella_id"] = None
    database.child("umbrella_stand").push(data)

def get_matched_umbrella_id(person_id):
    matched_data_ref = database.child('matched_data')
    matched_data = matched_data_ref.get()
    if matched_data:
        for data in matched_data:
            if data.get('person_id') == str(person_id):
                return data.get('umbrella_id')
    return None

def get_slot_number(umbrella_id):
    if umbrella_id is None:
        return None

    umbrella_stand_ref = database.child('umbrella_stand')
    query = umbrella_stand_ref.order_by_child('umbrella_id').equal_to(str(umbrella_id))
    umbrella_stand_data = query.get()

    if umbrella_stand_data:
        for entry in umbrella_stand_data.values():
            slot_number = entry.get('slot_number')
            if slot_number is not None:
                return slot_number

    query = umbrella_stand_ref.order_by_child('timestamp').limit_to_last(1)
    umbrella_stand_data = query.get()

    if umbrella_stand_data:
        for entry in umbrella_stand_data.values():
            slot_number = entry.get('slot_number')
            if slot_number is not None:
                return slot_number

    return None
