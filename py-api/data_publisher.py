from sensors.dht11 import DHT11
from sensors.mcp3008 import MCP3008
from db.db_connector import DBConnector
import RPi.GPIO
import time
import threading
from pathlib import Path

class Sensors:

    def __init__(self):
        RPi.GPIO.setwarnings(False)
        RPi.GPIO.setmode(RPi.GPIO.BCM)
        home = str(Path.home())
        self.__db = DBConnector(f'{home}/db/py_api.db')
        self.dht_instance = DHT11(18)
        self.mcp3008_instance = MCP3008(16)

    def start_temp(self):
        while True:
            dht = self.dht_instance.read_temp()
            print(dht, end='\r')
            time.sleep(1)

    def start_dust(self):
        while True:
            dust = self.mcp3008_instance.read_dust()
            # print(f'Dust: {dust}', end='\r')
            time.sleep(0.001)

    def start_noise(self):
        while True:
            noise = self.mcp3008_instance.read_noise()
            # print(f'Noise:{noise}', end='\r')
            time.sleep(0.0001)

    def start(self):
        t_1 = threading.Thread(target=self.start_temp)
        t_2 = threading.Thread(target=self.start_dust)
        t_3 = threading.Thread(target=self.start_noise)
        t_1.start()
        t_2.start()
        t_3.start()

        t_1.join()
        t_2.join()
        t_3.join()
        print('Threads started')


s = Sensors()

s.start()
