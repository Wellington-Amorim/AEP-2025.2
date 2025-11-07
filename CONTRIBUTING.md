# Guia de Contribuição

## Como contribuir

1. Faça um Fork do projeto
2. Crie uma Branch para sua Feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a Branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Padrões de Código

### Estilo de Código
- Use a formatação padrão do Java
- Utilize nomes significativos para variáveis e métodos
- Mantenha métodos concisos e focados
- Adicione comentários apenas quando necessário para explicar "por que" algo foi feito

### Princípios de Design
- Siga os princípios SOLID
- Mantenha classes coesas e com responsabilidade única
- Prefira composição sobre herança
- Utilize padrões de projeto quando apropriado

### Testes
- Escreva testes unitários para novas funcionalidades
- Mantenha a cobertura de testes acima de 80%
- Use mocks para dependências externas
- Siga o padrão AAA (Arrange, Act, Assert)

### Commits
- Use mensagens de commit claras e descritivas
- Siga o padrão: `tipo(escopo): mensagem`
- Tipos: feat, fix, docs, style, refactor, test, chore
- Mantenha commits pequenos e focados

### Pull Requests
- Descreva claramente as mudanças realizadas
- Inclua o contexto e motivação
- Referencie issues relacionadas
- Aguarde a revisão antes de fazer merge

## Fluxo de Desenvolvimento

1. Verifique se existe uma issue para a feature/bug
2. Discuta a implementação na issue
3. Implemente a solução em sua branch
4. Escreva/atualize os testes
5. Atualize a documentação
6. Abra o Pull Request
7. Responda aos comentários da revisão
8. Aguarde aprovação para merge

## Configuração do Ambiente

1. Java 17
2. Maven 3.8+
3. MySQL 8.0+
4. IDE de sua preferência (recomendamos IntelliJ IDEA ou Eclipse)

## Dúvidas?

Abra uma issue com a tag `dúvida` e ficaremos felizes em ajudar!