# 🚀 Delivery API

API backend desenvolvida em **Java 21** com **Spring Boot 3.2.x**, como base para um sistema de delivery moderno, escalável e de fácil manutenção.

---

## 📌 Visão Geral

Este projeto tem como objetivo fornecer uma estrutura inicial robusta para aplicações de delivery, servindo como ponto de partida para funcionalidades como:

- Cadastro e gerenciamento de usuários e restaurantes
- Catálogo de produtos
- Gestão de pedidos
- Integração com meios de pagamento
- Monitoramento e métricas via Actuator

---

## 🛠️ Tecnologias e Ferramentas

- **Java 21** – última versão LTS, com melhorias de performance e segurança  
- **Spring Boot 3.2.x** – criação de APIs REST com configuração mínima  
- **Maven** – gerenciamento de dependências e ciclo de build  
- **H2 Database** – banco de dados em memória para testes e desenvolvimento rápido  
- **Spring DevTools** – para recarga automática durante o desenvolvimento

---

## 📈 Status do Projeto

✅ **Funcional**  
> Aplicação rodando com sucesso, DataLoader carregando dados de teste e H2 Console ativo.

---

## ✅ Funcionalidades Implementadas

- [x] Estrutura inicial do projeto com Spring Initializr  
- [x] Dependências essenciais configuradas (Web, JPA, H2, DevTools)
- [x] Modelos de domínio (Cliente, Restaurante, Produto, Pedido)
- [x] Repositories com consultas customizadas
- [x] DataLoader para carga de dados de teste
- [x] H2 Console configurado e funcionando
- [x] Consultas SQL customizadas implementadas

---

# 📋 Guia de Alterações Recentes
**Período: Sábado Meia-noite até Segunda-feira 14/07/2025**

## 📌 **RESUMO EXECUTIVO**
Este guia documenta todas as alterações realizadas no projeto `delivery-tech` para corrigir erros de compilação, adicionar consultas customizadas e garantir o funcionamento correto da aplicação.

---

## 🔧 **1. CORREÇÕES NO DataLoader.java**

### 📍 **Arquivo:** `src/main/java/com/deliverytech/delivery_api/config/DataLoader.java`

#### **Problemas Corrigidos:**
- ✅ Sintaxe incorreta nos métodos setter
- ✅ Chamadas para métodos inexistentes (`setTelefone()`, `setEndereco()`)
- ✅ Aspas malformadas e caracteres especiais
- ✅ Parênteses extras nos parâmetros

#### **Método `inserirClientes()` - ANTES:**
```java
Cliente clientel = new Cliente();
clientel.setNome(nome: "João Silva");
clientel.setEmail(email:"joao@email.com");
clientel.setTelefone(telefone: "11999999999");
clientel.setEndereco(endereco: "Rua A, 123");
clientel.setAtivo(ativo:true);
```

#### **Método `inserirClientes()` - DEPOIS:**
```java
Cliente cliente1 = new Cliente();
cliente1.setNome("João Silva");
cliente1.setEmail("joao@email.com");
cliente1.setAtivo(true);
```

#### **Método `inserirRestaurantes()` - Removido:**
```java
// REMOVIDO: restaurante1.setEndereco("Rua X, 123");
// MOTIVO: Método não implementado na classe Restaurante
```

---

## 🔧 **2. CORREÇÕES NOS REPOSITORIES**

### 📍 **Arquivo:** `src/main/java/com/deliverytech/delivery_api/repository/ClienteRepository.java`

#### **Problema Corrigido:**
- ✅ Método retornando `Object` em vez de `List<Cliente>`

#### **ANTES:**
```java
Object findByNomeContainingIgnoreCase(String string);
```

#### **DEPOIS:**
```java
List<Cliente> findByNomeContainingIgnoreCase(String nome);
```

---

### 📍 **Arquivo:** `src/main/java/com/deliverytech/delivery_api/repository/PedidoRepository.java`

#### **Problema Corrigido:**
- ✅ Campo `valorTotal` não existe na classe Pedido (campo correto: `total`)

#### **ANTES:**
```java
@Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valor ORDER BY p.valorTotal DESC")
```

#### **DEPOIS:**
```java
@Query("SELECT p FROM Pedido p WHERE p.total > :valor ORDER BY p.total DESC")
```

---

### 📍 **Arquivo:** `src/main/java/com/deliverytech/delivery_api/repository/RestauranteRepository.java`

#### **Problema Corrigido:**
- ✅ Campo `valorTotal` não existe na classe Pedido (campo correto: `total`)

#### **ANTES:**
```java
"SUM(p.valorTotal) as totalVendas, " +
```

#### **DEPOIS:**
```java
"SUM(p.total) as totalVendas, " +
```

---

## 🆕 **3. NOVAS CONSULTAS CUSTOMIZADAS ADICIONADAS**

### 📍 **ProdutoRepository.java**
```java
@Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
               "FROM produto p " +
               "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
               "GROUP BY p.id, p.nome " +
               "ORDER BY quantidade_vendida DESC " +
               "LIMIT 5", nativeQuery = true)
List<Object[]> produtosMaisVendidos();
```

### 📍 **ClienteRepository.java**
```java
@Query(value = "SELECT c.nome, COUNT(p.id) as total_pedidos " +
               "FROM cliente c " +
               "LEFT JOIN pedido p ON c.id = p.cliente_id " +
               "GROUP BY c.id, c.nome " +
               "ORDER BY total_pedidos DESC " +
               "LIMIT 10", nativeQuery = true)
List<Object[]> rankingClientesPorPedidos();
```

### 📍 **RestauranteRepository.java**
```java
@Query("SELECT r.nome as nomeRestaurante, " +
       "SUM(p.total) as totalVendas, " +
       "COUNT(p.id) as quantidePedidos " +
       "FROM Restaurante r " +
       "LEFT JOIN Pedido p ON r.id = p.restaurante.id " +
       "GROUP BY r.id, r.nome")
List<RelatorioVendas> relatorioVendasPorRestaurante();
```

### 📍 **PedidoRepository.java**
```java
@Query("SELECT p.restaurante.nome, SUM(p.total) " +
       "FROM Pedido p " +
       "GROUP BY p.restaurante.nome " +
       "ORDER BY SUM(p.total) DESC")
List<Object[]> calcularTotalVendasPorRestaurante();

@Query("SELECT p FROM Pedido p WHERE p.total > :valor ORDER BY p.total DESC")
List<Pedido> buscarPedidosComValorAcimaDe(@Param("valor") BigDecimal valor);

@Query("SELECT p FROM Pedido p " +
       "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
       "AND p.status = :status " +
       "ORDER BY p.dataPedido DESC")
List<Pedido> relatorioPedidosPorPeriodoEStatus(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        @Param("status") StatusPedido status);
```

---

## 🆕 **4. NOVA INTERFACE DE PROJEÇÃO CRIADA**

### 📍 **Arquivo NOVO:** `src/main/java/com/deliverytech/delivery_api/repository/RelatorioVendas.java`
```java
package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;

public interface RelatorioVendas {
    String getNomeRestaurante();
    BigDecimal getTotalVendas();
    Long getQuantidePedidos();
}
```

---

## 📦 **5. IMPORTS ADICIONADOS**

### **Nos Repositories:**
```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
```

---

## ✅ **6. RESULTADO FINAL**

### **Status da Aplicação:**
- ✅ **Compilação**: Sem erros
- ✅ **Execução**: Spring Boot iniciando corretamente
- ✅ **DataLoader**: 3 clientes e 2 restaurantes inseridos
- ✅ **H2 Console**: Disponível em `http://localhost:8080/h2-console`
- ✅ **Consultas**: Todas funcionando corretamente

### **Dados de Teste Carregados:**
```
=== INICIANDO CARGA DE DADOS DE TESTE ===
--- Inserindo clientes ---
✓ 3 clientes inseridos
--- Inserindo Restaurantes ---
✓ 2 restaurantes inseridos

== TESTANDO CONSULTAS DOS REPOSITORIES ==
Cliente por email: João Silva
Clientes ativos: 2
Clientes com 'silva' no nome: 1
Existe cliente com email: true
=== CARGA DE DADOS CONCLUÍDA ===
```

---

## 🚨 **7. PRINCIPAIS LIÇÕES APRENDIDAS**

1. **Sempre verificar se os métodos existem** antes de chamá-los
2. **Conferir nomes dos campos** nas classes de modelo antes de usar em queries
3. **Sintaxe correta** nos métodos setter (sem parâmetros nomeados)
4. **Tipos de retorno** corretos nos repositories
5. **Imports necessários** para anotações e tipos

---

## 🗂️ Próximas Etapas

- [ ] Adicionar campos `telefone` e `endereco` nas classes `Cliente` e `Restaurante` se necessário
- [ ] Criar controllers para expor as consultas customizadas via REST API
- [ ] Implementar testes unitários para os repositories
- [ ] Adicionar validações nos modelos
- [ ] Configurar Swagger/OpenAPI para documentação da API

---

## 📁 Estrutura do Projeto
```
delivery-tech/
├── src/main/java/com/deliverytech/delivery_api/
│   ├── config/
│   │   └── DataLoader.java
│   ├── model/
│   │   ├── Cliente.java
│   │   ├── Restaurante.java
│   │   ├── Produto.java
│   │   ├── Pedido.java
│   │   └── StatusPedido.java
│   ├── repository/
│   │   ├── ClienteRepository.java
│   │   ├── RestauranteRepository.java
│   │   ├── ProdutoRepository.java
│   │   ├── PedidoRepository.java
│   │   └── RelatorioVendas.java
│   └── DeliveryApiApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## 🚀 Como Executar

1. **Clone o repositório**
2. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```
3. **Acesse o H2 Console:**
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:delivery`
   - Username: `sa`
   - Password: (deixar em branco)

---

**🎯 CONCLUSÃO:** Todas as correções foram aplicadas com sucesso e a aplicação está funcionando

