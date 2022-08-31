from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .chatbot_model_v5 import Chatbot


# Chatbot model initialize
chatbot = Chatbot()


# Chatbot answer response
@api_view(['POST'])
def stt_response(request):
    if request.method == 'GET':
        print('GET Accepted')
    elif request.method == 'POST':
        data = request.data
        print(f"question: {data['stt_Text']}")
        data['stt_Text'] = chatbot.answer(data['stt_Text'])
        print(f"answer: {data['stt_Text']}")
        return Response(data, status=status.HTTP_200_OK)
