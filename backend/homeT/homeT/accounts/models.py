from django.db import models
from django.core.validators import MinLengthValidator


class Accounts(models.Model):
    userID = models.CharField(max_length=20, primary_key=True, unique=True)
    userPW = models.TextField()
    userName = models.TextField()
    userBirth = models.IntegerField()
    userAddress = models.TextField(default='집')
