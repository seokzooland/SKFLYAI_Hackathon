from rest_framework import serializers
from .models import ChatBot


class ChatBotSerializer(serializers.ModelSerializer):
    class Meta:
        model = ChatBot
        fields = ('stt_text',)
