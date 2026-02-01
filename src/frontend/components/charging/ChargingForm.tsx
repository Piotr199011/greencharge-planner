import React from "react";

interface Props {
  hours: number
  onChange: (hours: number) => void
  onSubmit: () => void
}

const ChargingForm = ({ hours, onChange, onSubmit }: Props) => {
  return (
    <div>
      <label>
        Charging time (1–6h):
        <input
          type="number"
          min={1}
          max={6}
          value={hours}
          onChange={(e) => onChange(Number(e.target.value))}
          style={{ marginLeft: '8px' }}
        />
      </label>

      <button onClick={onSubmit} style={{ marginLeft: '12px' }}>
        Calculate
      </button>
    </div>
  )
}

export default ChargingForm
