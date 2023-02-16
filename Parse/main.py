import datetime
import os

import requests
import json
from bs4 import BeautifulSoup

filePath = 'moon_calendar.json'

test_date = datetime.datetime.strptime("01-7-2022", "%d-%m-%Y")
start = datetime.date(2023, 1, 1)

# 2050-01-01 - 9862
k = 9862

data = {}

for day in range(k):
    date = (start + datetime.timedelta(days=day)).isoformat()
    print(date)

    url = f'https://www.astrostar.ru/calendars/moon/{date}.html'
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    allDescription = soup.findAll('div', class_="mooncalendar-day-description")
    allTitle = soup.findAll('div', class_="title-box mt-5")
    allTable = soup.findAll('table', class_="mooncalendar-today-table")
    allMoonImageUrl = soup.findAll('div', class_='mooncalendar_image')

    for index, item in enumerate(allDescription):
        title = allTitle[index].find('h2').text.strip()
        desc = item.find('p').text.strip()
        moon_image_url = f"https://www.astrostar.ru{allMoonImageUrl[index].find('img')['src']}"
        tableAllRow = allTable[index].find('tbody').findAll('tr')

        table = []

        for row in tableAllRow:
            parameter = row.find_all('td', class_='parameter')[0].text.strip()
            value = row.find_all('td', class_='value')[0].text.strip()
            table.append(f'{value}-{parameter}')

        data.update({
            f'{date}_{index}': {'title': title, 'desc': desc, 'moon_image_url': moon_image_url,
                                'date': date, 'table': ';'.join(table)}
        })

if os.path.exists(filePath):
    os.remove(filePath)

with open(filePath, 'x') as fp:
    json.dump(data, fp)
