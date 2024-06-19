import hydra
from detect_and_trk import predict, checkout_queue
import match_umbrella_person
from ultralytics.yolo.utils import DEFAULT_CONFIG
import threading
from umbrella_stand_sensor import monitor_umbrella_stand
import time


@hydra.main(version_base=None, config_path=str(DEFAULT_CONFIG.parent), config_name=DEFAULT_CONFIG.name)
def main(cfg):
    # Start umbrella stand monitoring thread
    umbrella_stand_thread = threading.Thread(target=monitor_umbrella_stand)
    umbrella_stand_thread.daemon = True
    umbrella_stand_thread.start()

    # Start object detection and tracking thread
    detection_thread = threading.Thread(target=run_detection, args=(cfg,))
    detection_thread.daemon = True
    detection_thread.start()

    try:
        while True:
            # Keep the main thread running
            time.sleep(1)
    except KeyboardInterrupt:
        print("Program terminated by user")

def run_detection(cfg):
    while True:
        # Object Detection and Tracking
        cfg.source = 0
        detected_objects = predict(cfg)
        matched_results = match_umbrella_person.match_umbrella_person(detected_objects['umbrella'], detected_objects['person'])
        print("Matching Results:")
        for umbrella, person in matched_results:
            print(f"Umbrella {umbrella['center']} matched with Person {person['center']}")
            
        
        time.sleep(1)

if __name__ == "__main__":
    main()
