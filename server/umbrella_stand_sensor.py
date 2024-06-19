import RPi.GPIO as GPIO
import time
from firebase_api import save_umbrella_stand_data
from detect_and_trk import umbrella_in_stand
from detect_and_trk import umbrella_queue
from detect_and_trk import checkout_queue
from firebase_api import get_matched_umbrella_id, get_slot_number

GPIO.setwarnings(False)

# distance sensor pin
TRIG = 23
ECHO = 24

# servo motor pin
SERVO_PIN = 18

# global variable
slot_number = 1

# distance sensor setting
def setup_ultrasonic_sensor():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(TRIG, GPIO.OUT)
    GPIO.setup(ECHO, GPIO.IN)

# distance sensor measure function
def measure_distance():
    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False)
    pulse_start = None
    pulse_end = None

    while GPIO.input(ECHO) == 0:
        pulse_start = time.time()

    while GPIO.input(ECHO) == 1:
        pulse_end = time.time()

    if pulse_start is not None and pulse_end is not None:
        pulse_duration = pulse_end - pulse_start
        distance = pulse_duration * 17150
        distance = round(distance, 2)
        return distance
    else:
        return 0

# servo motor setting
def setup_servo_motor():
    GPIO.setup(SERVO_PIN, GPIO.OUT)

# servo motor control function
def control_servo_motor(angle):
    servo = GPIO.PWM(SERVO_PIN, 50)  # Servo motor PWM setting (50Hz)
    servo.start(0)  # Servo motor initialization
    duty = angle / 18 + 2  
    GPIO.output(SERVO_PIN, True)
    servo.ChangeDutyCycle(duty)
    time.sleep(1)
    GPIO.output(SERVO_PIN, False)
    servo.ChangeDutyCycle(0)

def monitor_umbrella_stand():
    global slot_number
    setup_ultrasonic_sensor()
    setup_servo_motor()
    # log file
    log_file = "distance.log"
    while True:
        distance = measure_distance()
        print(f"[Distance] {distance} cm")
        with open(log_file, "a") as f:
            f.write(f"{time.strftime('%Y-%m-%d %H:%M:%S')} - Distance: {distance} cm\n")
        
        umbrella_id = None
        
        if distance <= 10:
            print("[Umbrella] Detected!")
            
            if not umbrella_queue.empty():
                umbrella_id = umbrella_queue.get()
                print(f"umbrella_id: {umbrella_id}")
                
            # Lock umbrella_stand
            control_servo_motor(90)
            save_umbrella_stand_data(slot_number, "locked", distance, umbrella_id)
            
            if not checkout_queue.empty():
                checkout_person_id = checkout_queue.get()
                print(f"Person {checkout_person_id} is in the checkout area.")
        
                umbrella_id = get_matched_umbrella_id(checkout_person_id)
                if umbrella_id:
                    print(f"Matched umbrella ID for person {checkout_person_id}: {umbrella_id}")
                    
                    slot_number = get_slot_number(umbrella_id)
                    if slot_number:
                        print(f"Umbrella {umbrella_id} is in slot {slot_number}")
                
                        control_servo_motor(0)  
                        print(f"Unlocked slot {slot_number}")
                        
                        umbrella_id = None
                        save_umbrella_stand_data(slot_number, "unlocked", distance, umbrella_id)
        else:
            # Open umbrella_stand
            control_servo_motor(0)
            save_umbrella_stand_data(slot_number, "unlocked", distance, umbrella_id)

        
        time.sleep(1)

def main():
    try:
        monitor_umbrella_stand()
    except KeyboardInterrupt:
        GPIO.cleanup()

if __name__ == "__main__":
    main()
