from rest_framework import routers
from django.urls import path, include, re_path
from .views import stt_response

router = routers.DefaultRouter()

app_name = 'chatbot'
urlpatterns = [
    path('', include(router.urls)),
    path('', include('rest_framework.urls', namespace='rest_framework_category')),
    path('stt/', stt_response)
]
