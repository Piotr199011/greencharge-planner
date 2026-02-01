// src/components/charging/ChargingResult.tsx
import { OptimalChargingWindow } from '../../types/energy'
import { formatDateTime } from '../../utils/formatDate'
import { formatNumber } from '../../utils/formatNumber'
import React from 'react';

interface Props {
  result: OptimalChargingWindow
}

const ChargingResult = ({ result }: Props) => {
  return (
    <div style={{ marginTop: '16px' }}>
      <p><b>Start:</b> {formatDateTime(result.start)}</p>
      <p><b>End:</b> {formatDateTime(result.end)}</p>
      <p><b>Clean energy:</b> {formatNumber(result.averageCleanPercentage)}%</p>
    </div>
  )
}

export default ChargingResult
