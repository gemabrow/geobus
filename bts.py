#!/usr/bin/env python3
import json
import urllib.request

DEBUG = False
BTS_URL = "http://bts.ucsc.edu:8081/location/get"


def get_bus_data(target_url=BTS_URL):
    """ Getter function
    """
    return _fetch_data_(target_url)


def _fetch_data_(target_url):
    """ Specifics to targeted data handling go here
        Args:
             target_url: string indicating target url
    """
    data = []
    with urllib.request.urlopen(target_url) as url:
        data = json.load(url)
    return _parse_json_(data)

def _parse_json_(json_data):
    """ Specifics to targeted data handling go here
        Args:
    """
    # [{'id': '75', 'lon': -122.05508, 'lat': 36.998844, 'type': 'LOOP'},
    #  {'id': '80', 'lon': -122.05486, 'lat': 36.9913,   'type': 'LOOP'},
    #  {'id': '95', 'lon': -122.0644,  'lat': 36.999165, 'type': 'LOOP'},
    #  {'id': '90', 'lon': -122.05516, 'lat': 36.998234, 'type': 'LOOP'}]
    return json_data


def _parse_bus_(url_response):
    """ Specifics to targeted data handling go here
        Args:
             url_response: response from targeted url
    """
    pass
