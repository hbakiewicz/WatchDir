Program do wysy³ania plików Edi na adres wskazany w polu Email odbiorcy 

program monitoruje wskazany katalog w konfiguracji (config.properties) 
zapisie w tym katalogu pliku program wyszukuje w nim ci¹gu znaków "Nrdok:" 
Jeœli go znjdzie wyszukuje w bazie adresu email dla tego dokumnetu u¿ywaj¹æ zapytania 

select email from Kontrahent where kontrid = (select kontrid from dokkontr where dokid = (select dokid from dok where NrDok like '" + NrDok + "'
jeœli go znajdzie wysy³a go jako za³¹cznik pod wskazany adres 




Program jest udostêpniony tak jak jest bez ¿adnej odpowiedzialoœci za jego dzia³anie, masz uwagi pisz : mbakiewicz@gmail.com 