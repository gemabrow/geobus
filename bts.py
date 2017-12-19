#!/usr/bin/env python3
import json
import urllib.request

DEBUG = False
BTS_URL = "http://bts.ucsc.edu:8081/location/get"


def get_bus_data(target_url=BTS_URL):
    """ Getter function to be used by outside classes
    """
    return _fetch_data_(target_url)


def _fetch_data_(target_url):
    """ Specifics to targeted data handling go here
        Args:
             target_url: string indicating target url
    """
    # # assign user agent to header to disguise access ;)
    # chrome_header = {'User-Agent':
    #                 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 '
    #                 '(KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'}
    # url_response = requests.get(target_url, headers=chrome_header)
    data = []
    json_source = open('debug_data.json') \
        if DEBUG \
        else urllib.request.urlopen(target_url)
    data = json.load(json_source)
    # unsure of having to use json.dumps(data)
    return _parse_json_(data)


def _parse_json_(json_data):
    """ Specifics to targeted data handling go here
        Args:
             url_response: response from targeted url
    """
    pass


def _parse_bus_(url_response):
    """ Specifics to targeted data handling go here
        Args:
             url_response: response from targeted url
    """
    pass
