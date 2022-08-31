import mimetypes
# import os module
import os
# Import HttpResponse module
from django.http import HttpResponse, Http404


def download_file(request):
    # Define Django project base directory
    BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    # Define text file name
    filename = 'app-release.apk'
    # Define the full file path
    filepath = BASE_DIR + '/downloadapp/files/' + filename
    # Open the file for reading content
    if os.path.exists(filepath):
        with open(filepath, 'rb') as fh:
            response = HttpResponse(fh.read(), content_type="application/force_download")
            response['Content-Disposition'] = 'inline; filename=' + os.path.basename(filepath)
            return response
        # If file is not exists
    raise Http404
