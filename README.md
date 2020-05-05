# Covid19App
 Repositório teste para criação de aplicativo Android usando API 'https://api.covid19api.com/' para recuperação e apresentação dos dados.
 
 Oaplicativo utiliza componentes de arquitetura Android e requer o nível de API 26 para a execução da aplicação. Componentes como ViewModel e LiveData estão presentes no código.
 ```
 countryListApiViewModel.presentationCountryList.__observe__(this.viewLifecycleOwner, Observer {
     it?.let { list ->
         ...
     }
 })
```

### A tela principal do aplicativo
* Modo retrato

![image](https://user-images.githubusercontent.com/21205709/81075776-293e4300-8ec1-11ea-9918-3cc87bfd5679.png)

* Modo Paisagem

![image](https://user-images.githubusercontent.com/21205709/81076013-80441800-8ec1-11ea-914f-b053f3b9ee96.png)

* Outras Funções

| Ordenar Lista | Buscar Países |
|---------------|---------------|
| ![image](https://user-images.githubusercontent.com/21205709/81076340-ecbf1700-8ec1-11ea-9978-9909ded5638a.png) | ![image](https://user-images.githubusercontent.com/21205709/81076156-b1bce380-8ec1-11ea-8482-cbd21bc521d7.png) |
