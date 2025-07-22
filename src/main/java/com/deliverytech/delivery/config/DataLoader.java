package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

import java.util.Arrays;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO CARGA DE DADOS DE TESTE ===");

        // Limpar dados existentes
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        clienteRepository.deleteAll();

        // Inserir dados de teste
        inserirClientes();
        inserirRestaurantes();

        // Testar consultas
        testarConsultas();

        System.out.println("=== CARGA DE DADOS CONCLUÍDA ===");
        
        // ✅ ADICIONAR: Spring Boot iniciada com sucesso + Bender
        System.out.println("\n✅ Spring Boot Application iniciada com sucesso!");
        
        // 🤖 BENDER DIZENDO "AHHHH! Agora eu entendi!!!"
        System.out.println("\n\"AHHHH! Agora eu entendi!!!\"");
        System.out.println();
        System.out.println("                     $$$                       ");
        System.out.println("                    $   $                      ");
        System.out.println("                     $$$                       ");
        System.out.println("                     $ $                       ");
        System.out.println("                     $ $                       ");
        System.out.println("                   $$$ $$$                     ");
        System.out.println("                 $$  $$$  $$$                  ");
        System.out.println("               $$  $$$$$$$   $                 ");
        System.out.println("              $               $                ");
        System.out.println("             $                 $               ");
        System.out.println("             $                 $               ");
        System.out.println("             $     $$$$$$$$$$$$$$$             ");
        System.out.println("             $    $               $            ");
        System.out.println("             $    $   $$$$$$$$$$$$$            ");
        System.out.println("             $   $   $           $$$           ");
        System.out.println("             $   $   $ $$$   $$$  $$           ");
        System.out.println("             $   $   $ $$$   $$$  $$           ");
        System.out.println("             $   $   $           $$$           ");
        System.out.println("             $    $   $$$$$$$$$$$$$            ");
        System.out.println("             $     $$$$$$$$$$$$$$              ");
        System.out.println("             $                 $               ");
        System.out.println("             $    $$$$$$$$$$$$$$               ");
        System.out.println("             $   $  $  $  $  $                 ");
        System.out.println("             $  $$$$$$$$$$$$$$                 ");
        System.out.println("             $  $   $  $  $  $                 ");
        System.out.println("             $   $$$$$$$$$$$$$$$               ");
        System.out.println("            $$$                 $$$            ");
        System.out.println("          $$   $$$         $$$$$   $$          ");
        System.out.println("        $$        $$$$$$$$$          $$$       ");
        System.out.println("       $  $$                     $$$$   $$     ");
        System.out.println("    $$$$$   $$$$$$$$      $$$$$$$       $ $    ");
        System.out.println("  $      $$         $$$$$$              $ $$   ");
        System.out.println(" $    $    $                            $ $ $  ");
        System.out.println(" $     $   $              $$$$$$$$$$$   $ $ $$ ");
        System.out.println(" $$$    $   $  $$$$$$$$$$$$          $   $ $ $$");
        System.out.println("$   $$$$    $  $                     $   $ $$ $");
        System.out.println("$$$    $   $$  $                     $$  $ $  $");
        System.out.println("$   $  $  $$   $                      $  $$$  $");
        System.out.println("$     $$ $$    $               $$$    $  $ $  $");
        
        // ✅ INFORMAR sobre captura automática
        System.out.println("\n🎯 SISTEMA DE CAPTURA AUTOMÁTICA ATIVO!");
        System.out.println("📁 Respostas serão salvas em: ./entregaveis/");
        System.out.println("🔄 Faça requisições para /api/* e veja os arquivos sendo gerados!\n");
    }


private void inserirClientes() {
    System.out.println("--- Inserindo clientes ---");

    Cliente cliente1 = new Cliente();
    cliente1.setNome("João Silva");
    cliente1.setEmail("joao@email.com");
    cliente1.setAtivo(true);

    Cliente cliente2 = new Cliente();
    cliente2.setNome("Maria Santos");
    cliente2.setEmail("maria@email.com");
    cliente2.setAtivo(true);

    Cliente cliente3 = new Cliente();
    cliente3.setNome("Pedro Oliveira");
    cliente3.setEmail("pedro@email.com");
    cliente3.setAtivo(false);

    clienteRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3));
    System.out.println("✓ 3 clientes inseridos");
}

private void inserirRestaurantes() {
    System.out.println("--- Inserindo Restaurantes ---");

    Restaurante restaurantel = new Restaurante();
    restaurantel.setNome("Pizza Express");
    restaurantel.setCategoria("Italiana");
    restaurantel.setTelefone("1133333333");
    restaurantel.setTaxaEntrega(new BigDecimal("3.50"));
    restaurantel.setAtivo(true);

    Restaurante restaurante2 = new Restaurante();
    restaurante2.setNome("Burger King");
    restaurante2.setCategoria("Fast Food");
    restaurante2.setTelefone("1144444444");
    restaurante2.setTaxaEntrega(new BigDecimal("5.00"));
    restaurante2.setAtivo(true);

    restauranteRepository.saveAll(Arrays.asList(restaurantel, restaurante2));
    System.out.println("✓ 2 restaurantes inseridos");
}

private void testarConsultas() {
    System.out.println("\n== TESTANDO CONSULTAS DOS REPOSITORIES ==");

    // Teste ClienteRepository
    System.out.println("\nTestes ClienteRepository");
    
    var clientePorEmail = clienteRepository.findByEmail("joao@email.com"); 
    System.out.println("Cliente por email: " + clientePorEmail.map(Cliente::getNome).orElse("Não encontrado"));
    
    var clientesAtivos = clienteRepository.findByAtivoTrue();
    System.out.println("Clientes ativos: " + clientesAtivos.size());

    var clientesPorNome = clienteRepository.findByNomeContainingIgnoreCase("silva");
    System.out.println("Clientes com 'silva' no nome: " + clientesPorNome.size());

    boolean existeEmail = clienteRepository.existsByEmail("maria@email.com");
    System.out.println("Existe cliente com email: " + existeEmail);

}

}