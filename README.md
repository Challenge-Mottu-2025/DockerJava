<h1>Organização Mottu</h1>

<p>
  O projeto "Organização Mottu" tem como principal meta otimizar a organização das motos disponíveis nos pátios da Mottu, uma empresa que atua fortemente no ramo de mobilidade urbana. Com o crescimento da frota, torna-se cada vez mais desafiador manter um controle manual e visual de todos os veículos disponíveis, seus modelos, condições e localização.
</p>

<p>
  O projeto Java é feito a partir dos dados do sistemas, dados esses, que estão contidos no banco de dados Oracle do projeto. Futuramente, Java e C# serão integrados com front-end em uma aplicação mais robusta. O sistema back-end funciona para operações de CRUD e listagem de parâmetros, para garantir uma adaptação e manuseabilidade melhor dos dados mantidos, o que facilita o processo do projeto como um todo. 
</p>

<p>
 Neste projeto foi utilizado como principal ferramenta, o framework de Java, SpringBoot, para facilitar a interação com banco de dados e melhorar a produtiviado do projeto.  
</p>

<h2>Rodando o projeto</h2>

<h4>Requisitos mínimos:</h4>
<br></br>

* Postman ou Insomnia instalados
* Java instalado na máquina
* Git instalado na máquina
* IDE para Java na instalada na máquina
  
<br></br>
<h4>Instalação</h4>

1. Clone o repositório
   ```sh
   git clone https://github.com/Challenge-Mottu-2025/challenge-java.git
   ```
2. Altere a URL remota do Git para evitar envios acidentais ao projeto base
   ```sh
   git remote set-url origin https://github.com/Challenge-Mottu-2025/challenge-java.git.git
   git remote -v # Confirme as mudanças
   ```
3. Abra o projeto na sua IDE
   
4. Rode o projeto
   
5. Abra o Postman ou o Insomnia e digite a seguinte URL com método GET:
   ```sh
   http://localhost:8080/funcionarios
   ## ou
   http://localhost:8080/usuarios
   ```

<h2>Testes</h2>

Caso queira fazer um teste, a seguir, é um JSON para cadastrar um novo funcionário para o sistema:
```sh
   {
      "nome": "Linus Torvald",
      "cpf": "62242321222",
      "senha": "Linux71",
      "cep": 20140702
   }
   ```

Lembrete: Para fazer o cadastro de funcionários, você precisa alterar o método de GET para POST

<h2>Desenvolvedores</h2>

João Broggine - https://www.linkedin.com/in/joaobroggine/ | joaovitorbrogginelopes@gmail.com

João Vitor Cândido - https://www.linkedin.com/in/jvictor0507/

Lavinia Park - https://github.com/laviniapark
