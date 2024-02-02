# WeatherApp, pariprojekti

## Sisällysluettelo

- [WeatherApp, pariprojekti](#weatherapp-pariprojekti)
  - [Sisällysluettelo](#sisällysluettelo)
  - [Projektin luokkakaavio](#projektin-luokkakaavio)
  - [Luokkien vastuujako](#luokkien-vastuujako)
  - [Työnjako ryhmäläisten välillä](#työnjako-ryhmäläisten-välillä)
  - [Tunnetut Ongelmat tai puutteet](#tunnetut-ongelmat-tai-puutteet)
  - [Ohjelman toiminta](#ohjelman-toiminta)
  - [Vapaaehtoiset ominaisuudet](#vapaaehtoiset-ominaisuudet)
  - [Käyttöohje](#käyttöohje)

## Projektin luokkakaavio

Projektin kuvauksessa käytetty luokkkakaavio on toteutettu käyttäen PlantUML-kuvauskieltä(voidaan luoda suoraan käyttäen komentoa `mvn generate-test-sources`). Tästä lopullinen kuva on saatu aikaiseksi käyttäen [draw.io](https://app.diagrams.net/) verkkosivua.

![Prjektin luokkakaavio](./WeatherApp/Documentation/Ohjelman-rakenne.svg)

Kuva löytyy isompana [Documentation-kansiosta](./WeatherApp/Documentation/).

## Luokkien vastuujako

Kuten ylempänä käsittelimme, tämä ohjelma sisältää kuusi (6) erilaista luokkaa. Alempana taulukko siitä miten luokkien vastuu on jaettu.

| Luokan Nimi |  Vastuualue lyhyesti |
|---|---|
| Favourites | Pitää kirjaa nykyisestä ja suosikkipaikoista. |
| Location | Tiettyä paikkaa kuvaava luokka. |
| Weather | Tietyn paikan säätä kuvaava luokka. |
| WeatherAPIReader | Palauttaa dataa OpenWeatherApi:sta oikein formatoituna. |
| WeatherApp.java | UI-ikkunan toteuttava luokka. Sisältää main funktion. |
| WeatherAppException | Poikkeusluokka jota muut luokat hyödyntävät |

## Työnjako ryhmäläisten välillä

Työnjako löytyy helpoiten [Gitlabin issueista](https://course-gitlab.tuni.fi/compcs140-fall2023/group3194/-/issues), sillä hyödynsimme niitä työn jakamiseen. Mutta alla nopea taulukko työnjaosta.

| Tehtävä | Kenelle jaettiin | Kuka teki |
|---------|------------------|-----------|
| CI/CD-putken configurointi projektiin | Vili | Vili |
| Luokka Location | Joel | Joel |
| Luokka Favourites | Joel | Joel |
| Luokka Weather | Vili | Vili |
| Luokka WeatherAPIReader | Vili | Vili |
| Testit Weather-luokalle | Vili | Vili |
| Testit Location-luokalle | Joel | Joel |
| Testit Favourites-luokalle | Joel | Joel |
| Testit WeatherAPIReader-luokalle | Vili | Vili |
| Luokka WeatherAPP (eli siis UI:n teko) | Vili, Joel | Vili, Joel |
| Dokumentointi | Vili, Joel | Vili, Joel |

## Tunnetut Ongelmat tai puutteet

Kun suosikki valitaan suosikit pudotusvalikosta, tämänhetkinen sijainti päivittyy halutusti. Nyt suljetussa pudotusvalikossa lukee kyseinen sijainti. Kun sijainti poistetaan "Remove Favourite" napilla, pudotusvalikon teksti muuttuu kokonaan tyhjäksi, vaikka "comboBox.setPromptText("Favourites");" pitäisi päivittää oletustekstiksi "Favourites".

Useamman päivän sääikonit näyttävät vesisadetta, vaikka kuvissa pitäisi olla lumisadetta.

UI:ssa on kaikeinlaista säätämistä, kuten se että arvot pomppivat ympäri ruutua (huonolla tuurilla).

## Ohjelman toiminta

Ohjelma lukee JSON tiedostosta viimeisimmän paikan, kun ohjelma viimeksi suljettiin. Sen lisäksi luetaan samasta tiedostosta aiemmin lisätyt suosikit. Jos tiedostoa ei ole asetetaan ohjelmaan default sijainniksi Siilinjärvi. Tällöin suosikkilista asetetaan tyhjäksi.

Ohjelma näyttää tämänhetkisen sään sovelluksen yläosassa. Keskikohdassa näkyy tämänhetkisen ja seuraavan kuuden päivän alimman ja ylimmän lämpötilan. Ikoni näyttää visuaalisesti päivän sään. Ohjelman alaosa näyttää tuntikohtaisen ennusteen alkaen seuraavasta tunnista.

Ohjelma tallentaa ja poistaa sijainnin suosikeista, riippuen onko kyseinen sijainti jo suosikki vai ei. Lisäyksen tai poiston jälkeen sijaniti on lisätty tai poistettu "Favourites" pudotusvalikosta. Sijainniksi lisäävä nappi päivittyy niin, että käyttäjä tietää aina lisätäänkö vai poistetaanko sijainti suosikeista.

Ohjelma tallentaa myös käyttäjän hakemat sijaintitiedot. Jos kyseistä sijaintia ei ole olemassa tai sitä ei löydy, sijainti tallennetaan hakuhistoriaan - tällöin hakunapin painaminen ei tee mitään. Ohjelma hakee uutta sijaintia vasta "Search" nappulan painamisen jälkeen. Hakuhistoria tallennetaan omaan JSON tiedostoon, joka luetaan myös ohjelman käynnistyessä. Hakuhistoria voidaan tyhjentää "Clear History" napista, milloin historia tyhjennetään.

Ohjelmasta löytyy myös yksiköiden vaihtava nappi. Nappi vaihtaa mittayksikköä metrijärjestelmän ja Brittiläisen yksikköjärjestelmän välillä. 

## Vapaaehtoiset ominaisuudet

Ohjelmassa olevia lisäominaisuuksia ovat yksikkömuunnokset sekä hakuhistoria ja sen tyhjennys. Sovellus esittää oletuksena kaikki yksiköt metrijärjestelmänä. Käyttäjä voi vaihtaa yksikköä Brittiläiseen järjestelmään, milloin kaikki nykyiset ja tulevat tiedot esitetään kyseisessä muodossa. Myös uudet haut antavat tiedot senhetkisen asetuksen mukaisesti.

Ohjelma tallentaa käyttäjän hakuhistorian erilliselle JSON tiedostolle. Tyhjää hakua ei tallenneta, mutta muuten kaikki käyttäjän tekemät haut - myös sellaiset paikkakunnat joita ei ole olemassa - tallennetaan historiaan. Tälläinen hyödytön haku ei kuitenkaan tee mitään. 
Ohjelman sulkeutuessa historia tallennetaan ja se luetaan seuraavan käynnistyksen yhteydessä. Kun käyttäjä valitsee hakuhistoriasta olemassa olevan sijainnin, päivitetään se näytölle. Historian poisto tyhjentää hakuhistorian ajonaikaisesta muistista sekä siitä kirjoitetusta tiedostosta - tiedosto ylikirjoitetaan uudestaan ohjelman sulkeutuessa.

## Käyttöohje

Uuden sijainnin hakeminen ei toimi enter näppäimellä, vaan käyttäjän on painettava hiirellä "Search" nappulaa kirjoitettuaan sijainnin nimen. Jos sijainteja on monta samannimistä, näytetään vain ensimmäinen hakutulos.
"Remove Favourite" poistaa kyseisen sijainnin suosikeista.
"Add Favourite" lisää kyseisen sijainnin suosikkeihin.
"Imperial" vaihtaa metrijärjestelmän Brittiläiseen yksikköjärjestelmään.
"Metric" vaihtaa Brittiläisen yksikköjärjestelmän metrijärjestelmään.
"Favourites" pudotusvalikko listaa kaikki suosikit. Jos suosikkeja ei ole, lista on tyhjä.
"History" pudotusvalikko listaa käyttäjän haut. Jos haettuja sijainteja ei ole, lista on tyhjä.
"Clear History" tyhjentää "History" listan.