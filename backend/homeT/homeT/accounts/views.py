from django.shortcuts import render
from rest_framework import viewsets, filters

from .models import Accounts
from .serializers import AccountSerializer

from io import StringIO
from django.http import FileResponse


class AccountViewSet(viewsets.ModelViewSet):
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer


class AccountCheck(viewsets.ModelViewSet):
    search_fields = ['userPW']
    filter_backends = (filters.SearchFilter,)
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer


def address_search(request):
    return FileResponse(as_attachment=True, filename="index.html")
