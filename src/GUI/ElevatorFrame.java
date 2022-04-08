package GUI;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import ElevatorSubsystem.ElevatorStates;
import FloorSubsystem.FloorSystem;
import Messages.MessageTypes;

import javax.annotation.processing.Filer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;


public class ElevatorFrame extends JFrame implements ElevatorView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JSlider> elevator;
	private ArrayList<JTextField> status;
	private ArrayList<JTextField> currFloor;

	public ElevatorFrame() {
		// TODO Auto-generated constructor stub
		super("Elevator Simulation");
		elevator = new ArrayList<>();
		status = new ArrayList<>();
		currFloor = new ArrayList<>();
		
		GridLayout mainPanelLayout = new GridLayout(0,4);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(mainPanelLayout);
		
		mainPanel.add(createElevatorPanel(1));
		mainPanel.add(createElevatorPanel(2));
		mainPanel.add(createElevatorPanel(3));
		mainPanel.add(createElevatorPanel(4));
		
		add(mainPanel);
		
		
		
		try {
				File file = new File("src/GUI/elevator_music.wav");
				System.out.println(file.getAbsolutePath());
		       AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
		       Clip clip = AudioSystem.getClip( );
		       clip.open(audioInputStream);
		       clip.start( );
		} catch(Exception ex) {
		       System.out.println("Error with playing sound.");
		       ex.printStackTrace( );
		}
		
		setSize(1000, 800);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
	}
	
	private JPanel createElevatorPanel(int elevatorID) {
		GridLayout elevatorLayout = new GridLayout(4,1);
		Font headerFont = new Font("SansSerif", Font.BOLD, 40);
		Font statusFont = new Font("SansSerif", Font.BOLD, 15);
		
		
		JPanel elevatorPanel = new JPanel();
		elevatorPanel.setLayout(elevatorLayout);
		
		JTextField header = new JTextField("Elevator " + elevatorID);
		header.setFont(headerFont);
		header.setHorizontalAlignment(JTextField.CENTER);
		header.setBorder(null);
		header.setEditable(false);
		elevatorPanel.add(header);
		
		JSlider elevator = new JSlider(JSlider.VERTICAL, 1, 22, 1);
		elevator.setPaintTicks(true);
		elevator.setPaintLabels(true);
		elevator.setMinorTickSpacing(1);
		elevator.setMajorTickSpacing(2);
		elevator.setEnabled(false);
		this.elevator.add(elevator);
		elevatorPanel.add(elevator);
		
		
		JTextField status = new JTextField("Status: " + ElevatorStates.IDLE);
		status.setFont(statusFont);
		status.setHorizontalAlignment(JTextField.CENTER);
		status.setBorder(null);
		status.setEditable(false);
		this.status.add(status);
		elevatorPanel.add(status);
		
		JTextField currFloor = new JTextField("Current floor: " + 1);
		currFloor.setFont(statusFont);
		currFloor.setHorizontalAlignment(JTextField.CENTER);
		currFloor.setBorder(null);
		currFloor.setEditable(false);
		this.currFloor.add(currFloor);
		elevatorPanel.add(currFloor);
		
		return elevatorPanel;
	}

	@Override
	public void setCurrentFloor(int elevatorID, int currFloor) {
		JSlider elevator = this.elevator.get(elevatorID);
		JTextField currFloorField = this.currFloor.get(elevatorID);
		elevator.setValue(currFloor);
		this.elevator.set(elevatorID, elevator);
		currFloorField.setText("Current floor: " + Integer.toString(currFloor));
	}

	@Override
	public void setStatus(int elevatorID, ElevatorStates status) {
		JTextField statusField = this.status.get(elevatorID);
		statusField.setText("Status: " + status.name());
	}
	
	public static void main(String[] args) {
		ElevatorFrame eFrame = new ElevatorFrame();
	}

}
