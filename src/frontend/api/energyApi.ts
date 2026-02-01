import { DailyAverageMix, OptimalChargingWindow } from '../types/energy'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8082/api'

export const fetchEnergyMix = async (): Promise<DailyAverageMix[]> => {
  const response = await fetch(`${BASE_URL}/energy-mix`)

  if (!response.ok) {
    throw new Error('Failed to fetch energy mix')
  }

  return response.json()
}

export const fetchOptimalWindow = async (
  hours: number
): Promise<OptimalChargingWindow> => {
  const response = await fetch(
    `${BASE_URL}/optimal-charging?hours=${hours}`
  )

  if (response.status === 204) {
    throw new Error('No data')
  }

  if (!response.ok) {
    throw new Error('Failed to fetch optimal window')
  }

  return response.json()
}
