package GUI;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.*;

import ElevatorSubsystem.ElevatorStates;
import Messages.MessageTypes;

public class ElevatorFrame extends JFrame implements ElevatorView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JSlider> elevator;
	private ArrayList<JTextField> status;

	public ElevatorFrame() {
		// TODO Auto-generated constructor stub
		super("Elevator Simulation");
		elevator = new ArrayList<>();
		status = new ArrayList<>();
		GridLayout mainPanelLayout = new GridLayout(0,4);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(mainPanelLayout);
		
		mainPanel.add(createElevatorPanel(1));
		mainPanel.add(createElevatorPanel(2));
		mainPanel.add(createElevatorPanel(3));
		mainPanel.add(createElevatorPanel(4));
		
		add(mainPanel);
		
		setSize(1000, 800);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
	}
	
	private JPanel createElevatorPanel(int elevatorID) {
		GridLayout elevatorLayout = new GridLayout(3,1);
		Font headerFont = new Font("SansSerif", Font.BOLD, 40);
		Font statusFont = new Font("SansSerif", Font.BOLD, 15);
		
		
		JPanel elevatorPanel = new JPanel();
		elevatorPanel.setLayout(elevatorLayout);
		
		JTextField header = new JTextField("Elevator " + elevatorID);
		header.setFont(headerFont);
		header.setHorizontalAlignment(JTextField.CENTER);
		header.setBorder(null);
		elevatorPanel.add(header);
		
		JSlider elevator = new JSlider(JSlider.VERTICAL, 1, 22, 1);
		elevator.setPaintTicks(true);
		elevator.setPaintLabels(true);
		elevator.setMinorTickSpacing(1);
		elevator.setMajorTickSpacing(2);
		this.elevator.add(elevator);
		elevatorPanel.add(elevator);
		
		JTextField status = new JTextField("Status: " + ElevatorStates.IDLE);
		status.setFont(statusFont);
		status.setHorizontalAlignment(JTextField.CENTER);
		status.setBorder(null);
		this.status.add(status);
		elevatorPanel.add(status);
		
		return elevatorPanel;
	}

	@Override
	public void setCurrentFloor(int elevatorID, int currFloor) {
		JSlider elevator = this.elevator.get(elevatorID);
		elevator.setValue(currFloor);
		this.elevator.set(elevatorID, elevator);
	}

	@Override
	public void setStatus(int elevatorID, ElevatorStates status) {
		JTextField statusField = this.status.get(elevatorID);
		statusField.setText("Status: " + status.name());
	}

}
