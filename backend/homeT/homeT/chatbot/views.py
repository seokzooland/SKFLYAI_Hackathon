from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .chatbot_model_v5 import Chatbot


@api_view(['POST'])
def stt_response(request):
    if request.method == 'GET':
        print('GET Accepted')
    elif request.method == 'POST':
        chatbot = Chatbot()
        data = request.data
        print(data)
        data['stt_Text'] = chatbot.answer(data['stt_Text'])
        return Response(data, status=status.HTTP_200_OK)
