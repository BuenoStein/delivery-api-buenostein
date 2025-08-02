package com.deliverytech.delivery_api.exception;

public class ExceptionMessage {

  // É obrigatório
  public static final String EmailObrigatorio = "O e-mail é obrigatório.";
  public static final String NomeObrigatorio = "O nome é obrigatório.";
  public static final String TelefoneObrigatorio = "O telefone é obrigatório.";
  public static final String TelefoneInvalido = "O telefone deve ser um número válido com 10 a 15 dígitos, podendo iniciar com '+'.";
  public static final String EnderecoObrigatorio = "O endereço é obrigatório.";
  public static final String CategoriaObrigatoria = "A categoria é obrigatória.";
  public static final String DescricaoObrigatoria = "A descrição é obrigatória.";
  public static final String NumeroPedidoObrigatorio = "O número do pedido é obrigatório.";
  public static final String ItensPedidoObrigatorios = "Os itens são obrigatórios.";
  public static final String RestauranteObrigatorio = "O restaurante é obrigatório.";
  public static final String ClienteObrigatorio = "O cliente é obrigatório.";
  public static final String DataPedidoObrigatoria = "A data do pedido é obrigatória.";
  public static final String QuantidadeObrigatoria = "A quantidade é obrigatória.";
  public static final String ProdutoObrigatorio = "O produto é obrigatório.";
  public static final String DisponibilidadeProdutoObrigatoria = "A disponibilidade do produto é obrigatória.";
  public static final String TaxaEntregaObrigatoria = "A taxa de entrega é obrigatória.";
  public static final String StatusObrigatorio = "O status é obrigatório.";
  
  // Inválido
  public static final String EmailInvalido = "O e-mail informado é inválido.";
  public static final String RestauranteInvalido = "O restaurante informado é inválido.";
  public static final String TransicaoStatusPedidoInvalida = "Transição de status inválida para o pedido.";

  // Já cadastrado
  public static final String EmailJaCadastrado = "E-mail já cadastrado.";
  public static final String RestauranteJaCadastrado = "Restaurante já cadastrado.";

  // Não encontrado
  public static final String ClienteNaoEncontrado = "Cliente não encontrado.";
  public static final String RestauranteNaoEncontrado = "Restaurante não encontrado.";
  public static final String ProdutoNaoEncontrado = "Produto não encontrado.";
  public static final String PedidoNaoEncontrado = "Pedido não encontrado.";
  public static final String PedidosNaoEncontradosParaCliente = "Nenhum pedido encontrado para o cliente informado.";
  public static final String NenhumProdutoEncontrado = "Nenhum produto encontrado.";
  public static final String ProdutosNaoEncontradosParaRestaurante = "Nenhum produto encontrado para o restaurante informado.";
  public static final String ProdutosNaoEncontradosParaCategoria = "Nenhum produto encontrado para a categoria informado.";
  public static final String ProdutosNaoEncontradosParaPrecoMenor = "Nenhum produto encontrado com preço menor ou igual a: R$ {0}.";
  public static final String ProdutosNaoEncontradosParaFaixaPreco = "Nenhum produto encontrado na faixa de preço: R$ {0} a R$ {1}.";
  public static final String NenhumRestauranteEncontrado = "Nenhum restaurante encontrado.";
  public static final String RestaurantesNaoEncontradosParaCategoria = "Nenhum restaurante encontrado para a categoria informado.";
  public static final String RestaurantesNaoEncontradosParaFaixaPreco = "Nenhum restaurante encontrado na faixa de preço: R$ {0} a R$ {1}.";
  public static final String RestauranteNaoEncontradosParaPrecoMenor = "Nenhum restaurante encontrado com preço menor ou igual a: R$ {0}.";
  public static final String NenhumaVendaEncontrada = "Nenhum dado de vendas encontrado.";
  public static final String NenhumPedidoEncontrado = "Nenhum pedido encontrado.";
  
  // Inativo
  public static final String ClienteInativo = "O cliente está inativo.";
  public static final String RestauranteInativo = "O restaurante está inativo.";

  // Disponivel
  public static final String RestauranteNaoDisponivel = "O restaurante não está disponível.";

  // Outros
  public static final String PrecoDeveSerMaiorQueZero = "O preço deve ser maior que zero.";
  public static final String ProdutoNaoPertenceAoRestaurante = "O produto não pertence ao restaurante selecionado.";
  public static final String ProdutoNaoDisponivel = "O produto não está disponível.";
  public static final String PedidoNaoPodeSerCancelado = "O pedido não pode ser cancelado,  status atual: ";
  public static final String QuantidadeMaiorQueZero = "A quantidade deve ser pelo menos 1.";
  public static final String QuantidadeMenorQueCem = "A quantidade não pode ser maior que 100.";
  public static final String PedidoJaCancelado = "O pedido já está cancelado.";
  public static final String RestauranteJaInativo = "Restaurante já está inativo.";
}
