from django.shortcuts import render
from rest_framework import viewsets, filters

from .models import Accounts
from .serializers import AccountSerializer


class AccountViewSet(viewsets.ModelViewSet):
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer


class AccountCheck(viewsets.ModelViewSet):
    search_fields = ['userPW']
    filter_backends = (filters.SearchFilter,)
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer

