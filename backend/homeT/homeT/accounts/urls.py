from rest_framework import routers
from django.urls import path, include
from .views import AccountViewSet, AccountCheck, address_search, account_change

router = routers.DefaultRouter()
router.register('acclist', AccountViewSet)
router.register('accpwcheck', AccountCheck)

app_name = 'accounts'
urlpatterns = [
    path('', include(router.urls)),
    path('', include('rest_framework.urls', namespace='rest_framework_category')),
    path('address/', address_search),
    path('change/<str:pk>', account_change)
]
