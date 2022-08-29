from django.db import models


class ChatBot(models.Model):
    stt_text = models.TextField()
