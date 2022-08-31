from django.shortcuts import render
from rest_framework import viewsets, filters, status
from rest_framework.decorators import api_view
from rest_framework.response import Response

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


@api_view(['PUT'])
def account_change(request, pk):
    try:
        account = Accounts.objects.get(pk=pk)
    except Accounts.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    serializer = AccountSerializer(Accounts, data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
