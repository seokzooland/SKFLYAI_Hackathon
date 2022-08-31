import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from model.chatbot_old import Chatbot


text = '여보세요'

chatbot = Chatbot()

answer = chatbot.answer(text)
print(answer)