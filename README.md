<img width="1080" height="2400" alt="image" src="https://github.com/user-attachments/assets/63b1a957-a245-4198-bebd-821a34aead24" />
<img width="1080" height="2400" alt="image" src="https://github.com/user-attachments/assets/17eda659-b1ce-4d06-b152-40b0b51ede28" />

## Estrutura e injeção de dependências

Aplicativo Android com Kotlin, Jetpack Compose, Room e Hilt, organizado em:

- `data/`: banco Room, DAOs, repositórios e callback dos dados iniciais.
- `di/`: módulo Hilt que fornece o banco, os DAOs e o escopo de corrotinas da aplicação.
- `model/`: entidades e enums de projetos e tarefas.
- `viewmodel/`: estado e ações das telas, com `@HiltViewModel`.
- `ui/`: telas, componentes, navegação e tema Compose.

`TaskApplication` inicializa o Hilt com `@HiltAndroidApp` e `MainActivity` é o ponto de entrada com `@AndroidEntryPoint`. O fluxo de dependências é:

```text
DatabaseModule → AppDatabase → DAOs → Repositórios → ViewModels → Telas
```

O banco e os repositórios usam `@Singleton`. Os repositórios e ViewModels recebem suas dependências por construtores com `@Inject`. A navegação obtém os ViewModels com `hiltViewModel()`, mantendo o compartilhamento entre as listas e seus formulários no escopo da Activity.

O callback usa `Provider<AppDatabase>` para acessar o banco somente após sua criação e mantém o preenchimento inicial em um escopo de corrotinas da aplicação. O nome e a versão do banco foram preservados.

Para adicionar um repositório, use um construtor `@Inject`. Para dependências criadas por bibliotecas, adicione um método `@Provides` em um módulo de `di/`. Os testes unitários podem continuar instanciando os repositórios e ViewModels diretamente com DAOs falsos.

Configuração baseada na [documentação oficial do Hilt](https://dagger.dev/hilt/gradle-setup).

Validação:

```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

