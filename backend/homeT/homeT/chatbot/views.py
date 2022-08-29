from django.shortcuts import render
from django.http import JsonResponse
from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework.parsers import JSONParser
from .serializers import ChatBotSerializer
from .models import ChatBot


@api_view(['POST'])
def stt_response(request):
    if request.method == 'GET':
        print('GET Accepted')
    elif request.method == 'POST':
        data = request.data
        print(data)
        return Response(data, status=status.HTTP_200_OK)
