export interface DailyFuelMix {
  biomass: number
  nuclear: number
  hydro: number
  wind: number
  solar: number
  gas: number
  coal: number
  other: number
}

export interface DailyAverageMix {
  date: string
  averageMix: DailyFuelMix
  cleanPercentage: number
}

export interface OptimalChargingWindow {
  start: string
  end: string
  averageCleanPercentage: number
}
