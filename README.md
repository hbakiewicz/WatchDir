Program monitoruje wskazany katalog w konfiguracji (config.properties) 
zapisie w tym katalogu pliku program wyszukuje w nim ciągu znaków "Nrdok:" 
Jeżeli go znajdzie wyszukuje w bazie adresu email dla tego dokumentu używa  zapytania 

select email from Kontrahent where kontrid = (select kontrid from dokkontr where dokid = (select dokid from dok where NrDok like '" + NrDok + "'
jeśli go znajdzie wysyła go jako załącznik pod wskazany adres 




Program jest udostępniony tak jak jest bez żadnej odpowiedzialności za jego działanie, masz uwagi pisz : mbakiewicz@gmail.com
