package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; //ADICIONAR ESTE IMPORT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j // ADICIONAR ESTA ANOTAÇÃO
@Service
@RequiredArgsConstructor
@Transactional  // ADICIONADO: Para operações de escrita
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Override
    public Produto cadastrar(Produto produto) {
        // MELHORADO: Validar preço antes de cadastrar
        validarPreco(produto.getPreco());
        
        // MELHORADO: Definir disponível como true por padrão
        if (produto.getDisponivel() == null) {
            produto.setDisponivel(true);
        }
        
        return produtoRepository.save(produto);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Override
    public Produto atualizar(Long id, Produto atualizado) {
        return produtoRepository.findById(id)
            .map(produto -> {
                // MELHORADO: Validar preço se foi alterado
                if (atualizado.getPreco() != null) {
                    validarPreco(atualizado.getPreco());
                    produto.setPreco(atualizado.getPreco());
                }
                
                if (atualizado.getNome() != null) {
                    produto.setNome(atualizado.getNome());
                }
                if (atualizado.getDescricao() != null) {
                    produto.setDescricao(atualizado.getDescricao());
                }
                if (atualizado.getCategoria() != null) {
                    produto.setCategoria(atualizado.getCategoria());
                }
                
                return produtoRepository.save(produto);
            })
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    @Override
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado - ID: " + id);
        }
        produtoRepository.deleteById(id);
        log.info("Produto deletado - ID: {}", id); //Agora funciona
    }

    @Override
    public void inativar(Long id) {
        produtoRepository.findById(id)
            .ifPresentOrElse(
                produto -> {
                    produto.setDisponivel(false);
                    produtoRepository.save(produto);
                    log.info("Produto inativado - ID: {}", id); // ✅ Agora funciona
                },
                () -> {
                    throw new RuntimeException("Produto não encontrado - ID: " + id);
                }
            );
    }

    @Override
    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    @Override
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    @Override
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        produtoRepository.findById(id)
            .ifPresentOrElse(produto -> {
                produto.setDisponivel(disponivel);
                produtoRepository.save(produto);
            }, () -> {
                throw new RuntimeException("Produto não encontrado");
            });
    }

    @Override
    public void validarPreco(BigDecimal preco) {
        // CORRIGIDO: Assinatura void conforme interface
        if (preco == null) {
            throw new IllegalArgumentException("Preço não pode ser nulo");
        }
        
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        
        // ADICIONADO: Validação de preço máximo razoável
        BigDecimal precoMaximo = new BigDecimal("99999.99");
        if (preco.compareTo(precoMaximo) > 0) {
            throw new IllegalArgumentException("Preço não pode ser superior a R$ 99.999,99");
        }
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of(); // Retorna lista vazia se nome for nulo ou vazio
        }
        return produtoRepository.findByNomeContainingIgnoreCase(nome.trim());
    }
}
