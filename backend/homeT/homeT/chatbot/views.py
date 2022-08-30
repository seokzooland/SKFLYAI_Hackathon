from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .test_model import chatbot_model


@api_view(['POST'])
def stt_response(request):
    if request.method == 'GET':
        print('GET Accepted')
    elif request.method == 'POST':
        data = request.data
        print(data)
        data['stt_Text'] = chatbot_model(data['stt_Text'])
        return Response(data, status=status.HTTP_200_OK)
