import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import { PriorityLevel } from '@utils';
import Select from "react-select"
import { useState } from 'react';
import { BsSend } from "react-icons/bs"

const PriorityModal = ({ show, onHide, onConfirm }) => {
  const [priorityLevel, setPriorityLevel] = useState(null); 

  const priorityLevels = [
    { label: "LOW", value: PriorityLevel.LOW },
    { label: "MEDIUM", value: PriorityLevel.MEDIUM },
    { label: "HIGH", value: PriorityLevel.HIGH },
    { label: "URGENT", value: PriorityLevel.URGENT }
  ];

  const handleChange = (selectedLevel) => {
    setPriorityLevel(selectedLevel.value);
  };

  const handleClose = () => {
    setPriorityLevel(null);
    onHide();
  }

  const handleConfirm = () => {
    onConfirm(priorityLevel);
    handleClose();
  };

  return (
    <Modal show={show} onHide={handleClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>Assign Expert</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        <Select
          className="my-5 w-50 mx-auto"
          name="setPriority"
          onChange={handleChange}
          placeholder="Assign a priority" 
          options={priorityLevels}
          isMulti={false}
          value={priorityLevels.find((option) => option.value === priorityLevel)} 
        />
      </Modal.Body>

      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          Close
        </Button>
        <Button
          variant="primary"
          onClick={handleConfirm}
          disabled={priorityLevel === null} 
        >
          <BsSend size={15} className="me-2" />
          Submit
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default PriorityModal;
