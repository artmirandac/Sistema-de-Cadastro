import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Program {
    static List<Paciente> pacientes = new ArrayList<>();
    static List<Atendimento> atendimentos = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1 - Cadastrar Paciente");
            System.out.println("2 - Realizar Atendimento");
            System.out.println("3 - Listar Pacientes");
            System.out.println("4 - Listar Atendimentos por Data");
            System.out.println("5 - Número de Procedimentos em um Período");
            System.out.println("6 - Tempo Total de Duração em um Período");
            System.out.println("0 - Sair");

            System.out.print("Escolha uma opção: ");
            String escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    cadastrarPaciente(scanner);
                    break;
                case "2":
                    realizarAtendimento(scanner);
                    break;
                case "3":
                    listarPacientes();
                    break;
                case "4":
                    listarAtendimentosPorData(scanner);
                    break;
                case "5":
                    numeroProcedimentosEmPeriodo(scanner);
                    break;
                case "6":
                    tempoTotalDuracaoEmPeriodo(scanner);
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }

    static void cadastrarPaciente(Scanner scanner) {
        System.out.println("Cadastro de Paciente");
        System.out.print("Nome Completo: ");
        String nome = scanner.nextLine();

        System.out.print("Nome da Mãe: ");
        String nomeMae = scanner.nextLine();

        System.out.print("Data de Nascimento (yyyy-MM-dd): ");
        String dataNascimentoStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] dataParts = dataNascimentoStr.split("-");
        int ano = Integer.parseInt(dataParts[0]);
        int mes = Integer.parseInt(dataParts[1]);
        int dia = Integer.parseInt(dataParts[2]);
        Data dataNascimento = new Data(ano, mes, dia);

        System.out.print("Sexo (M/F): ");
        char sexo = scanner.nextLine().toUpperCase().charAt(0);

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        if (pacientes.stream().anyMatch(p -> p.getCPF().equals(cpf))) {
            System.out.println("CPF já cadastrado. Não é permitido cadastrar pacientes duplicados.");
            return;
        }

        pacientes.add(new Paciente(nome, nomeMae, dataNascimento, sexo, cpf));
        System.out.println("Paciente cadastrado com sucesso!");
    }

    static void realizarAtendimento(Scanner scanner) {
        System.out.println("Realizar Atendimento");
        System.out.print("Digite Nome, Data de Nascimento ou CPF do Paciente: ");
        String busca = scanner.nextLine();

        Paciente paciente = pacientes.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(busca) ||
                        p.getDataNascimento().toString().equals(busca) ||
                        p.getCPF().equals(busca))
                .findFirst()
                .orElse(null);

        if (paciente == null) {
            System.out.println("Paciente não encontrado. Cadastre um novo paciente.");
            cadastrarPaciente(scanner);
            return;
        }

        System.out.print("Data do Atendimento (yyyy-MM-dd): ");
        String dataAtendimentoStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] dataParts = dataAtendimentoStr.split("-");
        int ano = Integer.parseInt(dataParts[0]);
        int mes = Integer.parseInt(dataParts[1]);
        int dia = Integer.parseInt(dataParts[2]);
        Data dataAtendimento = new Data(ano, mes, dia);

        System.out.println("Procedimentos disponíveis:");
        System.out.println("1 - Raios – X de Tórax em PA");
        System.out.println("2 - Ultrassonografia Obstétrica");
        System.out.println("3 - Ultrassonografia de Próstata");
        System.out.println("4 - Tomografia");

        System.out.print("Escolha o procedimento (1-4): ");
        int escolhaProcedimento = Integer.parseInt(scanner.nextLine());

        if (escolhaProcedimento < 1 || escolhaProcedimento > 4) {
            System.out.println("Opção inválida. Tente novamente.");
            return;
        }

        Procedimento procedimento;
        switch (escolhaProcedimento) {
            case 1:
                procedimento = new Procedimento("Raios-X de Tórax em PA", "XXXXXXXXXX", 15);
                break;
            case 2:
                procedimento = new Procedimento("Ultrassonografia Obstétrica", "XXXXXXXXXX", 30);
                break;
            case 3:
                procedimento = new Procedimento("Ultrassonografia de Próstata", "XXXXXXXXXX", 45);
                break;
            case 4:
                procedimento = new Procedimento("Tomografia", "XXXXXXXXXX", 60);
                break;
            default:
                procedimento = null;
                break;
        }

        if (procedimento == null) {
            System.out.println("Procedimento inválido. Tente novamente.");
            return;
        }

        // Verificar restrições para realização de Tomografia
        if (escolhaProcedimento == 4) {
            if (atendimentos.stream()
                    .anyMatch(a -> a.getPaciente() == paciente &&
                            (a.getProcedimento().getNome().equals("Ultrassonografia Obstétrica") ||
                                    a.getProcedimento().getNome().equals("Ultrassonografia de Próstata")) &&
                            dataAtendimento.diffDias(a.getData()) <= 90)) {
                System.out.println("Tomografia não pode ser realizada. O paciente realizou Ultrassonografia Obstétrica ou Ultrassonografia de Próstata nos últimos três meses.");
                return;
            }
        }

        atendimentos.add(new Atendimento(paciente, dataAtendimento, procedimento));
        System.out.println("Atendimento registrado com sucesso!");
    }

    static void listarPacientes() {
        System.out.println("Lista de Pacientes:");
        pacientes.forEach(paciente -> System.out.println(paciente.getNome() + " - " + paciente.getDataNascimento().toString()));
    }

    static void listarAtendimentosPorData(Scanner scanner) {
        System.out.print("Digite a data para listar os atendimentos (yyyy-MM-dd): ");
        String dataConsultaStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] dataParts = dataConsultaStr.split("-");
        int ano = Integer.parseInt(dataParts[0]);
        int mes = Integer.parseInt(dataParts[1]);
        int dia = Integer.parseInt(dataParts[2]);
        Data dataConsulta = new Data(ano, mes, dia);

        List<Atendimento> atendimentosData = atendimentos.stream()
                .filter(a -> a.getData().compareTo(dataConsulta) == 0)
                .toList();

        if (atendimentosData.isEmpty()) {
            System.out.println("Nenhum atendimento registrado na data " + dataConsultaStr + ".");
        } else {
            System.out.println("Atendimentos na data " + dataConsultaStr + ":");
            atendimentosData.forEach(atendimento -> System.out.println(atendimento.getPaciente().getNome() + " - " + atendimento.getProcedimento().getNome()));
        }
    }

    static void numeroProcedimentosEmPeriodo(Scanner scanner) {
        System.out.print("Digite a data de início do período (yyyy-MM-dd): ");
        String inicioPeriodoStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] inicioParts = inicioPeriodoStr.split("-");
        int anoInicio = Integer.parseInt(inicioParts[0]);
        int mesInicio = Integer.parseInt(inicioParts[1]);
        int diaInicio = Integer.parseInt(inicioParts[2]);
        Data inicioPeriodo = new Data(anoInicio, mesInicio, diaInicio);

        System.out.print("Digite a data de fim do período (yyyy-MM-dd): ");
        String fimPeriodoStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] fimParts = fimPeriodoStr.split("-");
        int anoFim = Integer.parseInt(fimParts[0]);
        int mesFim = Integer.parseInt(fimParts[1]);
        int diaFim = Integer.parseInt(fimParts[2]);
        Data fimPeriodo = new Data(anoFim, mesFim, diaFim);

        List<Atendimento> atendimentosPeriodo = atendimentos.stream()
                .filter(a -> a.getData().compareTo(inicioPeriodo) >= 0 && a.getData().compareTo(fimPeriodo) <= 0)
                .toList();

        System.out.println("Número de Procedimentos realizados entre " + inicioPeriodoStr + " e " + fimPeriodoStr + ": " + atendimentosPeriodo.size());
    }

    static void tempoTotalDuracaoEmPeriodo(Scanner scanner) {
        System.out.print("Digite a data de início do período (yyyy-MM-dd): ");
        String inicioPeriodoStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] inicioParts = inicioPeriodoStr.split("-");
        int anoInicio = Integer.parseInt(inicioParts[0]);
        int mesInicio = Integer.parseInt(inicioParts[1]);
        int diaInicio = Integer.parseInt(inicioParts[2]);
        Data inicioPeriodo = new Data(anoInicio, mesInicio, diaInicio);

        System.out.print("Digite a data de fim do período (yyyy-MM-dd): ");
        String fimPeriodoStr = scanner.nextLine();
        // Tratamento básico da data para simplificar
        String[] fimParts = fimPeriodoStr.split("-");
        int anoFim = Integer.parseInt(fimParts[0]);
        int mesFim = Integer.parseInt(fimParts[1]);
        int diaFim = Integer.parseInt(fimParts[2]);
        Data fimPeriodo = new Data(anoFim, mesFim, diaFim);

        List<Atendimento> atendimentosPeriodo = atendimentos.stream()
                .filter(a -> a.getData().compareTo(inicioPeriodo) >= 0 && a.getData().compareTo(fimPeriodo) <= 0)
                .toList();

        int duracaoTotal = atendimentosPeriodo.stream()
                .mapToInt(a -> a.getProcedimento().getDuracao())
                .sum();

        System.out.println("Tempo Total de Duração entre " + inicioPeriodoStr + " e " + fimPeriodoStr + ": " + duracaoTotal + " minutos");
    }
}

class Data {
    private int ano;
    private int mes;
    private int dia;

    public Data(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public int compareTo(Data outraData) {
        if (this.ano != outraData.ano) {
            return Integer.compare(this.ano, outraData.ano);
        } else if (this.mes != outraData.mes) {
            return Integer.compare(this.mes, outraData.mes);
        } else {
            return Integer.compare(this.dia, outraData.dia);
        }
    }

    public int diffDias(Data outraData) {
        return Math.abs(this.dia - outraData.dia);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", ano, mes, dia);
    }
}

class Paciente {
    private String nome;
    private String nomeMae;
    private Data dataNascimento;
    private char sexo;
    private String cpf;

    public Paciente(String nome, String nomeMae, Data dataNascimento, char sexo, String cpf) {
        this.nome = nome;
        this.nomeMae = nomeMae;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public Data getDataNascimento() {
        return dataNascimento;
    }

    public char getSexo() {
        return sexo;
    }

    public String getCPF() {
        return cpf;
    }
}

class Procedimento {
    private String nome;
    private String codigo;
    private int duracao;

    public Procedimento(String nome, String codigo, int duracao) {
        this.nome = nome;
        this.codigo = codigo;
        this.duracao = duracao;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getDuracao() {
        return duracao;
    }
}

class Atendimento {
    private Paciente paciente;
    private Data data;
    private Procedimento procedimento;

    public Atendimento(Paciente paciente, Data data, Procedimento procedimento) {
        this.paciente = paciente;
        this.data = data;
        this.procedimento = procedimento;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Data getData() {
        return data;
    }

    public Procedimento getProcedimento() {
        return procedimento;
    }
}
