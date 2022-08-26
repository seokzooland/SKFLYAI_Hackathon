import sys
import os
import time

sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from model.chatbot import Chatbot


text = '여보세요'

chatbot = Chatbot()

answer = chatbot.predict(text)
print(answer)