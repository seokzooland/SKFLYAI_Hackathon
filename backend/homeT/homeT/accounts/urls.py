from rest_framework import routers
from django.urls import path, include, re_path
from .views import AccountViewSet, AccountCheck

router = routers.DefaultRouter()
router.register('acclist', AccountViewSet)
router.register('accpwcheck', AccountCheck)

app_name = 'accounts'
urlpatterns = [
    path('', include(router.urls)),
    path('', include('rest_framework.urls', namespace='rest_framework_category'))
]
