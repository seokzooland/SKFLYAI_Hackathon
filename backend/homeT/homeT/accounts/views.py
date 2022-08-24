from django.shortcuts import render
from rest_framework import viewsets

from .models import Accounts
from .serializers import AccountSerializer


class AccountViewSet(viewsets.ModelViewSet):
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer
    