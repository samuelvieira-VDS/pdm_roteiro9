<img width="1080" height="2400" alt="image" src="https://github.com/user-attachments/assets/63b1a957-a245-4198-bebd-821a34aead24" />
<img width="1080" height="2400" alt="image" src="https://github.com/user-attachments/assets/17eda659-b1ce-4d06-b152-40b0b51ede28" />

## Arquitetura limpa e Hilt

O projeto separa o domínio em um módulo Kotlin/JVM independente. A direção das dependências é:

```text
app (UI/ViewModels) → domain (casos de uso, modelos e contratos)
app (data/Room)     → domain (implementa os contratos)
app (di/Hilt)      → conecta casos de uso e implementações
```

### Domínio (`domain/`)

- `model/`: modelos de projetos e tarefas sem anotações de persistência.
- `repository/`: interfaces que definem o acesso aos dados.
- `usecase/`: observar, consultar, salvar e excluir projetos e tarefas.
- `validation/`: erros de validação por campo, sem mensagens de interface.

Os casos de uso de salvamento exigem título e categoria para tarefas e nome e cliente para projetos, além de normalizar espaços nos textos. Essas regras valem para qualquer consumidor do domínio. IDs vazios em consultas retornam `null`; excluir um registro ausente não altera os dados.

O domínio depende apenas de Kotlin/JVM e corrotinas. Ele não conhece Android, Compose, Room, Hilt, ViewModels ou implementações dos repositórios. O módulo pode ser testado isoladamente com `./gradlew :domain:test`.

### Aplicação (`app/`)

- `data/local/entity/`: entidades exclusivas do Room; tabelas e colunas existentes foram mantidas.
- `data/mapper/`: conversão entre entidades de persistência e modelos de domínio.
- `data/repository/`: implementações Room das interfaces do domínio.
- `data/`: banco, DAOs, conversores e callback dos dados iniciais.
- `viewmodel/`: depende dos casos de uso; publica estado, erros de validação e falhas das operações.
- `ui/`: telas, componentes, navegação e tema. Os formulários exibem os erros do domínio; formatação e conversão de entrada numérica permanecem na apresentação.
- `di/`: módulos Hilt para banco/DAOs, vínculos dos repositórios com `@Binds` e criação dos casos de uso com `@Provides`.

`TaskApplication` usa `@HiltAndroidApp`, `MainActivity` usa `@AndroidEntryPoint` e os ViewModels usam `@HiltViewModel`. Banco e implementações de repositório têm escopo `@Singleton`. A navegação obtém os ViewModels com `hiltViewModel()` no escopo da Activity, compartilhando estado entre listas e formulários.

Os construtores dos casos de uso não têm anotações de DI: a criação cabe ao módulo Hilt da aplicação. Assim, a infraestrutura pode mudar sem modificar as regras de negócio.

O callback mantém os dados iniciais usando `Provider<AppDatabase>` e um escopo de corrotinas da aplicação. Nome e versão do banco foram preservados.

### Validação

```bash
./gradlew :domain:test :app:assembleDebug :app:testDebugUnitTest
```

Os testes do domínio verificam validação, normalização e exclusão. Os testes de ViewModel usam repositórios falsos que implementam os contratos do domínio, cobrindo CRUD e recuperação após falhas, sem depender de Room ou de um contêiner Hilt.
