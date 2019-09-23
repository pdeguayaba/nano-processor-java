import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.*;

/**
 * *
 * Interamerican University of Puerto Rico, Aguadilla Campus
 * COMP 3500 Operating Systems
 * Professor Jose Navarro
 * Section: 70857
 * Module 2: Interrupts
 * Project: Nano processor design for a virtual computer (Due Date: 9/22/2019)
 * *
 * Group 2:
 * @author DIAZ LOPEZ, DUSTIN A.
 * @author ESCOBAR LASSALLE, BRYAN R.
 * @author ESTRELLA AYALA, EDWIN J.
 * *
 * Completed: 9/6/2019
 * *
 * * *
 * * * *
 * * * * *
 * This class contains the main method and also contains
 * all logic for both the GUI (In the beginning) and Simulator (Go to main) is
 * in this class
 */
 
public class NanoProcessorSimulation extends JPanel {
    /**
     * Declaration of global variables for the GUI
     */
    private final String DEFAULT_TEXT_GO = "The program will run from start to end";
    private final String TEXT_STEP = "The program will run step-by-step";
    private JButton run = new JButton("RUN");
    private JButton reset = new JButton("RESET");
    private JLabel headLabel;
    private JTextField fileName;

    /**
     * Declaration of global variables for the nano-processor simulation
     */
    private Scanner scanner = new Scanner(System.in);

    /**
     * address locations up to twenty (20)
     */
    private final String[] address = new String[]{
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "0A", "0B", "0C", "0D", "0E", "0F", "10", "11", "12", "13"
    };

    /**
     * This section sets up all the buttons and text fields (GUI) and all the logic for the buttons.
     * Not the code for the "nano processor simulation" (go to main)
     * @author Dustin Diaz
     * @since 9/3/2019
     */
    private NanoProcessorSimulation() {
        headLabel = new JLabel(DEFAULT_TEXT_GO, SwingConstants.CENTER);
        headLabel.setFont(headLabel.getFont().deriveFont(Font.BOLD, 24));

        JPanel titlePanel = new JPanel();
        titlePanel.add(headLabel);

        JRadioButton stepRadioButton = new JRadioButton("STEP");
        JRadioButton goRadioButton = new JRadioButton("GO");

        //Adds both buttons to a group
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(stepRadioButton);
        radioButtonGroup.add(goRadioButton);

        //sets go as the default selected button
        goRadioButton.setSelected(true);

        stepRadioButton.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) headLabel.setText(TEXT_STEP);
        });

        goRadioButton.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) headLabel.setText(DEFAULT_TEXT_GO);
        });

        JPanel informationPanel = new JPanel();
        informationPanel.add(new JLabel("File with memory contents:"));
        fileName = new JTextField(20);

        String file = "\\myCode.txt";
        String path = new File("").getAbsolutePath();

        path += file;

        System.out.println("Is this correct? " +
                "\n\t\"" + path + "\" " +
                "\n\tIf not change it in the text field!\n");

        fileName.setText(path);

        informationPanel.add(fileName);
        informationPanel.add(Box.createHorizontalStrut(20));
        informationPanel.add(new JLabel("Choose:"));
        informationPanel.add(goRadioButton);
        informationPanel.add(stepRadioButton);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));
        northPanel.add(titlePanel);
        northPanel.add(informationPanel);

        JPanel southBtnPanel = new JPanel(new GridLayout(3, 1, 1, 1));
        southBtnPanel.add(run);
        southBtnPanel.add(reset);
        JButton exit = new JButton("EXIT");
        southBtnPanel.add(exit);

        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(400, 400))); //empty placeholder
        add(southBtnPanel, BorderLayout.SOUTH);

        //Runs the simulation setup
        ActionListener runAction = actionEvent -> {
            reset.setEnabled(false);
            exit.setEnabled(false);
            run.setEnabled(false);
            readFileForRAMContent(fileName.getText(), goRadioButton.isSelected());
            reset.setEnabled(true);
            exit.setEnabled(true);
        };

        //simulates run button getting clicked when the 'ENTER' key is pressed
        Action enterKeyRunAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run.doClick();
            }
        };

        //re-enabled the run button and resets the title label
        ActionListener resetAction = actionEvent -> {
            run.setEnabled(true);
            if (!headLabel.getText().equals(TEXT_STEP)) headLabel.setText(DEFAULT_TEXT_GO);
        };

        ActionListener exitAction = actionEvent -> System.exit(1);

        //Textfield run action on key pressed 'ENTER'
        fileName.addActionListener(enterKeyRunAction);

        //Buttons action
        run.addActionListener(runAction);
        reset.addActionListener(resetAction);
        exit.addActionListener(exitAction);
    }

    /**
     * Sets up the initial JFrame and its logic (GUI)
     * @author Dustin Diaz
     * @since 9/3/2019
     */
    private static void createAndShowGui() {
        NanoProcessorSimulation simulationSetupPanel = new NanoProcessorSimulation();
        JFrame frame = new JFrame("Nano Processor Simulator");
        frame.setPreferredSize(new Dimension(600, 250));

        //closes window
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        //terminated program on request
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //puts all content on the frame
        frame.getContentPane().add(simulationSetupPanel);
        frame.pack();
        frame.setLocationByPlatform(true);

        //puts the frame at the middle of the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * After this documentation comment the rest of the code will be about the simulation
     * <p>
     * @param args the command line arguments
     * @author Dustin Diaz
     * @since 9/3/2019
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(NanoProcessorSimulation::createAndShowGui);
    }
    /**
     * Global variables
     * Initialization of standard instructions
     */
    private final String LOAD = "0001";
    private final String STORE = "0010";
    private final String ADD = "0101";
    private final String STOP = "0000";
    private final String CLEAR = "0100";
    private final String MULT = "0110";
    /**
     * Stores the values for the RAM simulation and handles all logic
     */
    private HashMap<String, String> RAM;

    /**
     * @param fileName file name or location of the file to be read
     * @param go determines if the program will run 'continuous' (true) or 'step-by-step' (false)
     * <p>
     * Reads the inputted file for the simulation of memory then if no errors occurs while parsing the
     * file it will initialize the RAM HashMap above and the simulation will begin
     * @author Dustin Diaz
     * @author Bryan Escobar
     * @author Edwin Estrella
     * @since 9/5/2019
     */
    private void readFileForRAMContent(String fileName, boolean go) {
        //variable declaration
        FileReader reader;
        List<String> fileContent = new ArrayList<>();
        int counter = 0;
        String[] ram = new String[20];

        //try catch for when an error occurs while reading the file
        try {
            //variable initialization
            reader = new FileReader(fileName);
            Scanner fileInput = new Scanner(reader);

            //reads each line of the file
            while (fileInput.hasNextLine()) {
                fileContent.add(fileInput.next());
                counter++;

                //if it exceeds the length of the ram array length it it stopped
                if (counter == ram.length) break;
            }

            /*
             * if there are less than twenty (20) instructions then it
             * fills the list with the STOP (0000) instruction.
             */
            if (counter < ram.length) for (int i = 0; i < ram.length - counter; i++) fileContent.add(STOP);

            //sets the values for the ram array
            for (int i = 0; i < ram.length; i++) ram[i] = fileContent.get(i);

            //initializes the RAM HashMap
            RAM = mapToMemoryAddress(ram);

            System.out.println("File with memory contents: " + this.fileName.getText());
            System.out.println("Memory loaded");
            if (go) System.out.println("Choice: GO");
            else System.out.println("Choice: STEP");
            System.out.println();

            //runs the simulation
            runSimulation(go);

        } catch (FileNotFoundException e) {
            //When an error occurs it will notify the user
            headLabel.setText("File error (see console)");
            //print the error
            e.printStackTrace();
            //and suggest a file path
            System.err.println("\nUse a path similar to this:\n\t"
                    + new File("").getAbsolutePath() + "\\myCode.txt\n\tOr just \"myCode.txt\"");
        }
    }

    /**
     * @param ram string array that contains the simulated content for memory address
     * @return returns the mapped values of ram to an address
     * <p>
     * simulates the relation between memory address and memory content and initialises the simulated RAM.
     * @author Dustin Diaz
     * @since 9/5/2019
     */
    private HashMap<String, String> mapToMemoryAddress(String[] ram) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < ram.length; i++) map.put(address[i], ram[i]);
        return map;
    }

    /**
     * @param runSpeed determines if the program will run 'continuous' (true) or 'step-by-step' (false)
     * @code code for the simulation
     * @author Dustin Diaz
     * @author Bryan Escobar
     * @author Edwin Estrella
     * @since 9/6/2019
     */
    private void runSimulation(boolean runSpeed) {
        HashMap<String, String> commandMeaning = initCmdMen();

        int PC = 0;
        String AC = "";
        String IR = "";

        String instruction;
        String content;
        printState(PC, AC, IR, RAM);

        instructions:
        while (PC <= address.length) {
            instruction = getInstructionFromRAM(address[PC]);
            content = getContentFromRAM(address[PC]);
            content = Operation.binToHex(content);
            System.out.println("----------------------------------------------------\n");

            /*
             * prompts the user to hit a button if the parameter is false (STEP)
             */
            promptEnterKey(runSpeed);

            System.out.println("Fetching instruction at address "
                    + formatRegister(address[PC])
                    + " [" + RAM.get(address[PC]) + "]");
            System.out.print("Decoding instruction: " + commandMeaning.get(instruction) + " ");

            IR = RAM.get(address[PC]);

            switch (instruction) {
                case LOAD:
                    System.out.print("from address " + format("000", content)
                            + " [" + getRAMContent(content) + "]\n");
                    System.out.println("Executing instruction\n");
                    AC = getRAMContent(content);
                    break;
                case STORE:
                    System.out.print("to " + format("000", content) + "\n");
                    System.out.println("Executing instruction\n");
                    RAM.put(format("00", content), format("0000", AC));
                    break;
                case ADD:
                    System.out.print("from address " + format("000", content) + " ["
                            + getRAMContent(content) + "]\n");
                    System.out.println("Executing instruction\n");
                    AC = Operation.add(AC, getRAMContent(content));
                    break;
                case STOP:
                    PC++;
                    System.out.print("execution\n");
                    System.out.println("Executing instruction\n");
                    printState(PC, AC, IR, RAM);
                    break instructions;
                case CLEAR:
                    System.out.println("AC");
                    System.out.println("Executing instruction\n");
                    AC = "0000";
                    break;
                case MULT:
                    System.out.print("by [" + format("000", content) + "]\n");
                    System.out.println("Executing instruction\n");
                    AC = format("0000", Operation.mult(AC, content));
                    break;
            }

            PC++;
            printState(PC, AC, IR, RAM);

        }
        System.out.println("\nEnd of execution\n");
        System.out.println("----------------------------------------------------\n");
    }

    /**
     * @param go run speed of the program. (false = STEP, true = GO)
     * <p>
     * simple method that takes in a value, but it is not saved
     * this method is used for the STEP functionality. So that the
     * program stops once it reaches the line with this method if
     * the parameter fits the description.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private void promptEnterKey(boolean go) {
        if (!go) {
            System.out.print("Press \"ENTER\" to fetch/decode/execute the next instruction > ");
            scanner.nextLine();
        }
    }

    /**
     * @param IR instruction register value
     * @return returns the content in ram related to a location in ram
     * <p>
     * First, the instruction register value is converted (from binary) to its hexadecimal value.
     * Second, it it formatted to that if is has less than (2) two characters then a (0) zero is
     * added at the beginning of the string. Third, the new string value is searched for on the
     * simulated RAM. The Content at the specified location is returned if none is found it returns null.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String getRAMContent(String IR) {
        return RAM.get(formatRegMemAdd(IR));
    }

    /**
     * @param replace filler text
     * @param register String to be filled by the replace parameter if it is less than it
     * @return returns a String with requested parameters
     * <p>
     * Takes a string and formats it to the desired characters ex: if replace = 0000 and register = 32
     * it will return 0032 or if register is 615023 it will return 5023 it will return the last amount of
     * the length of the replace string.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String format(String replace, String register) {
        return Operation.format(replace, register);
    }

    /**
     * @return returns a hash map of required instructions
     * <p>
     * Initializes a HashMap in the runSimulator method so that when an instruction value is
     * passed in the HashMap it returns the name of the inputted instruction.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private HashMap<String, String> initCmdMen() {
        HashMap<String, String> map = new HashMap<>();
        map.put(LOAD, "LOAD");
        map.put(STORE, "STORE");
        map.put(ADD, "ADD");
        map.put(STOP, "STOP");
        map.put(CLEAR, "CLEAR");
        map.put(MULT, "MULT");
        return map;
    }

    /**
     * @param memAddress memory address
     * @return returns the instruction related to the memory address
     * <p>
     * Takes in the memory address finds it in the RAM and gets the content associated with the memory address
     * and returns the binary value of the instruction.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String getInstructionFromRAM(String memAddress) {
        return Operation.hexToBin(RAM.get(memAddress)).substring(0, 4);
    }

    /**
     * @param memAddress memory address
     * @return returns the values after the instruction related to the memory address
     * <p>
     * Takes in the memory address finds it in the RAM and gets the content associated with the memory address
     * and returns the binary value of the values after the instruction.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String getContentFromRAM(String memAddress) {
        return Operation.hexToBin((RAM.get(memAddress))).substring(4);
    }

    /**
     * @param RAM current relation between content and its memory address
     * <p>
     * prints out the memory address and the content related to it from the simulated memory.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private void printMemoryAddressAndContent(HashMap<String, String> RAM) {
        Map<String, String> map = new TreeMap<>(RAM);
        String address;
        String content;
        System.out.println("Memory\nAddress\t\tContent");
        for (Map.Entry entry : map.entrySet()) {
            address = entry.getKey().toString();
            content = entry.getValue().toString();
            System.out.println("  " + address + "\t\t[" + content + "]");
        }

    }

    /**
     * @param PC simulated program counter
     * @param AC simulated accumulator register
     * @param IR simulated instruction register
     * <p>
     * prints out values for all simulated registers (parameters)
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private void printCPURegisters(int PC, String AC, String IR) {
        System.out.println("CPU Registers");
        System.out.println("[" + formatRegister(PC) + "] " + " PC");
        System.out.println("[" + formatRegister(AC) + "] " + " AC");
        System.out.println("[" + formatRegister(IR) + "] " + " IR");
        System.out.println();
    }

    /**
     * @param register meant for PC, AC, and IR
     * @return returns a formatted register with a length of four (4) characters
     * <p>
     * returns a String with a length of four (4) if the length is greater than you the last four characters
     * are selected and returned
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String formatRegister(int register) {
        return format("0000", String.valueOf(register));
    }

    /**
     * @param register meant for PC, AC, and IR
     * @return returns a formatted register with a length of four (4) characters
     * <p>
     * returns a String with a length of four (4) if the length is greater than you the last four characters
     * are selected and returned
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String formatRegister(String register) {
        return format("0000", register);
    }

    /**
     * @param register meant for PC, AC, and IR
     * @return returns a formatted register with a length of four (4) characters
     * <p>
     * returns a String with a length of four (4) if the length is greater than you the last four characters
     * are selected and returned
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private String formatRegMemAdd(String register) {
        return format("00", register);
    }

    /**
     * @param PC  simulated program counter
     * @param AC  simulated accumulator register
     * @param IR  simulated instruction register
     * @param RAM current relation between content and its memory address
     * <p>
     * prints out the CPU registers and the memory content with the address in memory for the content
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    private void printState(int PC, String AC, String IR, HashMap<String, String> RAM) {
        printCPURegisters(PC, AC, IR);
        printMemoryAddressAndContent(RAM);
    }
}

/**
 * This class handles converting hexadecimal, binary and decimal values:
 *
 * This class also does arithmetic operations with two hexadecimal values and 
 * will return the result as an hexadecimal value
 * 
 * @author Dustin Diaz
 * @since 9/4/2019
 */
class Operation {
    /**
     * @param binary takes in a binary value to be converted
     * @return returns Decimal value of the binary value
     * <p>
     * Converts the binary value to a decimal value
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static int binToDec(String binary) {
        return Integer.parseInt(binary, 2);
    }

    /**
     * @param binary takes in a binary value to be converted
     * @return returns the hexadecimal value of the binary value.
     * <p>
     * it first converts the binary value to its decimal value and then the decimal
     * value to its hexadecimal value.
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static String binToHex(String binary) {
        return decToHex(binToDec(binary));
    }

    /**
     * @param hexadecimal the hexadecimal value to be converted to binary
     * @return returns the binary value of the hexadecimal
     * <p>
     * it converts the hexadecimal value to its binary value
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static String hexToBin(String hexadecimal) {
        StringBuilder binary = new StringBuilder();

        for (char character : hexadecimal.toCharArray()) {
            switch (character) {
                case '0':
                    binary.append("0000");
                    break;
                case '1':
                    binary.append("0001");
                    break;
                case '2':
                    binary.append("0010");
                    break;
                case '3':
                    binary.append("0011");
                    break;
                case '4':
                    binary.append("0100");
                    break;
                case '5':
                    binary.append("0101");
                    break;
                case '6':
                    binary.append("0110");
                    break;
                case '7':
                    binary.append("0111");
                    break;
                case '8':
                    binary.append("1000");
                    break;
                case '9':
                    binary.append("1001");
                    break;
                case 'A':
                case 'a':
                    binary.append("1010");
                    break;
                case 'B':
                case 'b':
                    binary.append("1011");
                    break;
                case 'C':
                case 'c':
                    binary.append("1100");
                    break;
                case 'D':
                case 'd':
                    binary.append("1101");
                    break;
                case 'E':
                case 'e':
                    binary.append("1110");
                    break;
                case 'F':
                case 'f':
                    binary.append("1111");
                    break;
                default:
                    System.err.print("Invalid hexadecimal digit " + character + "! It was ignored...");
            }
        }

        return binary.toString();
    }

    /**
     * @param hexadecimal the hexadecimal value to be converted to decimal
     * @return returns the decimal value of the hexadecimal
     * <p>
     * it first converts the hexadecimal value to a binary value then the binary value to its decimal value
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static int hexToDec(String hexadecimal) {
        return Integer.parseInt(hexToBin(hexadecimal), 2);
    }

    /**
     * @param decimal takes in the int value or decimal value to be converted to hexadecimal
     * @return returns the hexadecimal value of the decimal value
     * <p>
     * Converts the decimal value to its hexadecimal value
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static String decToHex(int decimal) {
        int rem;
        StringBuilder hex = new StringBuilder();
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (decimal > 0) {
            rem = decimal % 16;
            hex.insert(0, hexChars[rem]);
            decimal = decimal / 16;
        }
        return hex.toString();
    }

    /**
     * @param decimal takes in the int value or decimal value to be converted
     * @return returns the binary value of the decimal value
     * <p>
     * Converts the decimal value to its binary value
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static String decToBin(int decimal) {
        return hexToBin(decToHex(decimal));
    }

    /**
     * @param replace filler text
     * @param value   String to be filled by the replace parameter if it is less than it
     * @return returns a String with requested parameters
     * <p>
     * Takes a string and formats it to the desired characters ex: if replace = 0000 and value = 32
     * it will return 0032 or if value is 615023 it will return 5023 it will return the last amount of
     * the length of the replace string.
     * @author Dustin Diaz
     * @since 9/6/2019
     */
    static String format(String replace, String value) {
        if (value.length() <= replace.length()) {
            return (replace + "").substring(0, replace.length() - value.length()) + value;
        } else {
            return value.substring(value.length() - replace.length());
        }
    }

    /**
     * @param hex1 parameter to be multiplied by second parameter
     * @param hex2 parameter to be multiplied by first parameter
     * @return returns the hex value of the multiplication of the two decimal value of the hexadecimal values
     * <p>
     * both parameters are first converted into decimal then they are both multiplied
     * the decimal solution is then converted into hexadecimal.
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static String mult(String hex1, String hex2) {
        return decToHex(hexToDec(hex1) * hexToDec(hex2));
    }

    /**
     * @param hex1 parameter to be added by second parameter
     * @param hex2 parameter to be added by first parameter
     * @return returns the hex value of the addition of the two decimal value of the hexadecimal values
     * <p>
     * both parameters are first converted into decimal then they are both added
     * the decimal solution is then converted into hexadecimal.
     * @author Dustin Diaz
     * @since 9/4/2019
     */
    static String add(String hex1, String hex2) {
        return decToHex(hexToDec(hex1) + hexToDec(hex2));
    }
}