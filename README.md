Program do wysy�ania plik�w Edi na adres wskazany w polu Email odbiorcy 

program monitoruje wskazany katalog w konfiguracji (config.properties) 
zapisie w tym katalogu pliku program wyszukuje w nim ci�gu znak�w "Nrdok:" 
Je�li go znjdzie wyszukuje w bazie adresu email dla tego dokumnetu u�ywaj�� zapytania 

select email from Kontrahent where kontrid = (select kontrid from dokkontr where dokid = (select dokid from dok where NrDok like '" + NrDok + "'
je�li go znajdzie wysy�a go jako za��cznik pod wskazany adres 




Program jest udost�pniony tak jak jest bez �adnej odpowiedzialo�ci za jego dzia�anie, masz uwagi pisz : mbakiewicz@gmail.com 