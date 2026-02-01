import React, { useEffect, useState } from 'react'
import Header from "../components/layout/Header"
import Footer from "../components/layout/Footer"
import EnergyPieChart from "../components/charts/EnergyPieChart"
import ChargingForm from "../components/charging/ChargingForm"
import ChargingResult from "../components/charging/ChargingResult"

import { fetchEnergyMix, fetchOptimalWindow } from '../api/energyApi'
import { DailyAverageMix, OptimalChargingWindow } from '../types/energy'
import { formatMixForChart } from '../utils/formatNumber'

const HomePage = () => {
  const [mix, setMix] = useState<DailyAverageMix[]>([])
  const [hours, setHours] = useState(3)
  const [result, setResult] = useState<OptimalChargingWindow | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchEnergyMix()
      .then(res => setMix(Array.isArray(res) ? res : []))
      .catch(() => setError('Data download error'))
  }, [])

  const handleSubmit = async () => {
    setError(null)
    try {
      const res = await fetchOptimalWindow(hours)
      setResult(res)
    } catch {
      setError('Failed to calculate loading window')
    }
  }

  return (
    <>
      <Header />

      <section>
        <h2>Energy mix (4 days)</h2>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '32px' }}>
          {mix.map(day => (
            <div key={day.date} style={{ flex: '1 1 300px', minWidth: 250 }}>
              <h3>{day.date}</h3>
              <p>Clean energy: {day.cleanPercentage.toFixed(2)}%</p>
              <EnergyPieChart data={formatMixForChart(day.averageMix)} />
            </div>
          ))}
        </div>
      </section>

      <section style={{ marginTop: '48px' }}>
        <h2>Optimal charging</h2>
        <ChargingForm
          hours={hours}
          onChange={setHours}
          onSubmit={handleSubmit}
        />
        {result && <ChargingResult result={result} />}
        {error && <p style={{ color: 'red' }}>{error}</p>}
      </section>

      <Footer />
    </>
  )
}

export default HomePage
