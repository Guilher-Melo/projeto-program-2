# Sistema de Gestão de Restaurante e Pedidos

## Integrantes

1) Bruno Rodrigues de Souza - bruno36399@gmail.com
2) João Vítor de Souza Santos - jvfacr2019@gmail.com
3) Guilherme de Souza Melo - guilherme.dsouza87@gmail.com
4) Luiz Eduardo Negri Silva Calábria - luiz.negri@ufrpe.br
5) Zaque Mateus Neves da Silva - zaque.neves@ufrpe.br

## Descrição

Este sistema tem como objetivo gerenciar o atendimento em um restaurante, incluindo cadastro de clientes, mesas, cardápio e pedidos. Ele deve permitir que garçons registrem os pedidos realizados em cada mesa, vinculem itens do cardápio, acompanhem o status da preparação e registrem os pagamentos.

O sistema deve organizar as mesas do restaurante, controlando quais estão livres, ocupadas ou reservadas. O cardápio deve conter itens organizados por categorias (entradas, pratos principais, sobremesas, bebidas) com preço e descrição. Os pedidos devem registrar itens, quantidade, status (em preparação, pronto, entregue) e valor total.

Relatórios de vendas por período, por item ou por mesa devem estar disponíveis, com possibilidade de exportação em PDF ou CSV para análise de desempenho do restaurante.

## Requisitos Funcionais

### 1. Gerenciamento de Clientes

- **REQ01**: Permitir o cadastro de clientes, incluindo nome, telefone e e-mail.
- **REQ02**: Manter histórico de consumo por cliente (pedidos anteriores e valores gastos).

### 2. Gerenciamento de Mesas

- **REQ03**: Permitir o cadastro de mesas, com número ou identificação única e capacidade de lugares.
- **REQ04**: Controlar status da mesa (livre, ocupada, reservada, em limpeza).

### 3. Gerenciamento de Cardápio

- **REQ05**: Permitir o cadastro de itens do cardápio com nome, descrição, preço e categoria (entrada, prato principal, bebida etc.).
- **REQ06**: Atualizar disponibilidade de itens (disponível, indisponível).

### 4. Registro de Pedidos

- **REQ07**: Permitir a criação de pedidos vinculados a uma mesa e/ou cliente.
- **REQ08**: Permitir a inclusão de múltiplos itens do cardápio em um pedido, com quantidade e observações.
- **REQ09**: Calcular automaticamente o valor total do pedido.
- **REQ10**: Atualizar o status do pedido (em preparação, pronto, entregue, cancelado).

### 5. Pagamentos

- **REQ11**: Permitir o registro de pagamentos vinculados a pedidos.
- **REQ12**: Suportar diferentes métodos de pagamento (dinheiro, cartão, PIX).
- **REQ13**: Permitir a divisão da conta entre diferentes clientes na mesma mesa.

### 6. Reservas de Mesas

- **REQ14**: Permitir que clientes façam reservas de mesas informando data, horário e número de pessoas.
- **REQ15**: Bloquear automaticamente mesas reservadas nos horários definidos.
- **REQ16**: Cancelar reservas apenas com antecedência mínima de 1 hora.

### 7. Relatórios e Estatísticas

- **REQ17**: Gerar relatório de vendas por período (diário, semanal, mensal).
- **REQ18**: Gerar relatório de itens mais vendidos do cardápio.
- **REQ19**: Gerar relatório de ocupação de mesas.
- **REQ20**: Permitir exportação de relatórios em **PDF** e **CSV**, com filtros e agrupamentos.

### 8. Regras e Restrições

- **REQ21**: Não permitir pedidos em mesas não ocupadas ou reservadas.
- **REQ22**: Não permitir fechamento da conta sem que todos os itens do pedido tenham sido entregues ou cancelados.
- **REQ23**: Não permitir reserva de mesa além da capacidade definida.

## Possíveis APIs/Bibliotecas a Serem Usadas

- **JavaFX** – Interface gráfica para gestão de mesas, pedidos e relatórios.
- **JDBC / Hibernate** – Persistência de dados em banco relacional.
- **Java Time API** – Controle de datas, horários e reservas.
- **iText / JasperReports** – Geração de relatórios em PDF.
- **Apache POI** – Exportação de relatórios em CSV ou Excel.
- **JUnit / Mockito** – Testes de regras de negócio (pedidos, reservas e pagamentos).
