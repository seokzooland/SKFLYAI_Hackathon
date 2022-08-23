from django.db import models


class Post(models.Model):

    userID = models.CharField(max_length=20, default='uid')
    userPW = models.CharField(max_length=20, default='upw')
    userName = models.CharField(max_length=20, default='name')
    userBirth = models.IntegerField(default=0)

