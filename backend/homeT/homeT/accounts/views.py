from django.shortcuts import render
from rest_framework import viewsets, filters

from .models import Accounts
from .serializers import AccountSerializer

# User Account apply into Database
class AccountViewSet(viewsets.ModelViewSet):
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer

# Password check
class AccountCheck(viewsets.ModelViewSet):
    search_fields = ['userPW']
    filter_backends = (filters.SearchFilter,)
    queryset = Accounts.objects.all()
    serializer_class = AccountSerializer

# Address search
def address_search(request):
    return render(request, "address.html")
